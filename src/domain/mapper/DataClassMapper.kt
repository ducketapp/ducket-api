package dev.ducketapp.service.domain.mapper

import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

typealias Mapper<I, O> = (I) -> O
typealias ParameterProvider<I, O> = (I) -> O

/**
 * Mapper that can convert one data class into another data class.
 *
 * @param <I> sourceType (convert from)
 * @param <O> targetType (convert to)
 */
class DataClassMapper<I : Any, O : Any>(
    private val sourceType: KClass<I>,
    private val targetType: KClass<O>,
) : Mapper<I, O> {

    companion object {
        // reified constructor that does not require passing of KClass attributes
        inline operator fun <reified I : Any, reified O : Any> invoke() = DataClassMapper(I::class, O::class)

        fun <I : Any, O : Any> collectionMapper(mapper: Mapper<I, O>) = object : Mapper<Collection<I>, Collection<O>> {
            override fun invoke(data: Collection<I>): Collection<O> = data.map(mapper)
        }
    }

    val fieldMappers = mutableMapOf<String, Mapper<Any, Any>>()                 // special mappers for target parameters
    val fieldMappings = mutableMapOf<String, String>()                          // source and target parameters names mapping
    val fieldTransformers = mutableMapOf<String, ParameterProvider<I, Any?>>()  // source and target parameters names mapping
    val fieldProviders = mutableMapOf<String, ParameterProvider<I, Any?>>()     // helps to specify target parameters which are not in source
    private val targetConstructor = targetType.primaryConstructor!!

    /**
     * @throws java.lang.reflect.InvocationTargetException if types don't match
     */
    override fun invoke(data: I): O = with(targetConstructor) {
        val args = parameters.associateWith {
            argFor(it, data)
        }

        callBy(args)
    }

    override fun toString(): String = "DataClassMapper($sourceType -> $targetType)"

    // get value of source data via reflection or call registered converter
    private fun argFor(parameter: KParameter, sourceData: I): Any? {
        val parameterName = parameter.name

        // get and return a value with help of provider if exists
        if (parameterName != null) {
            if (fieldProviders.containsKey(parameterName)) {
                val result = fieldProviders[parameterName]!!.invoke(sourceData)

                if (!parameter.type.isMarkedNullable && result == null) {
                    throw IllegalArgumentException("Cannot assign null to a non-nullable '${parameterName}' parameter")
                }

                return result
            }

            // get the mapping if present, or use default, to get source data parameter value
            val value = (fieldMappings[parameterName] ?: parameterName).let { sourceParameterName ->
                if (fieldTransformers.containsKey(sourceParameterName)) {
                    return@let fieldTransformers[sourceParameterName]!!.invoke(sourceData)
                }

                return@let sourceData.getParameterValue(sourceParameterName)
            }

            // return a result from special mapper for source value if exists
            if (fieldMappers.containsKey(parameterName) && value != null) {
                return fieldMappers[parameterName]?.invoke(value)
            }

            return value
        } else {
            return null
        }
    }

    inline fun <reified S : Any, reified T : Any> register(target: String, crossinline mapper: Mapper<S, T>): DataClassMapper<I, O> {
        return apply {
            this.fieldMappers[target] = object : Mapper<Any, Any> {
                override fun invoke(data: Any): Any = mapper.invoke(data as S)
            }
        }
    }

    inline fun <reified S : Any, reified T : Any> register(target: KProperty1<O, T?>, crossinline mapper: Mapper<S, T>): DataClassMapper<I, O> {
        return apply {
            this.fieldMappers[target.name] = object : Mapper<Any, Any> {
                override fun invoke(data: Any): Any = mapper.invoke(data as S)
            }
        }
    }

    fun map(source: String, target: String): DataClassMapper<I, O> {
        return apply {
            this.fieldMappings[target] = source
        }
    }

    fun <S : Any> map(source: String, target: KProperty1<S, Any?>): DataClassMapper<I, O> {
        return apply {
            this.fieldMappings[target.name] = source
        }
    }

    fun provide(target: String, value: Any?): DataClassMapper<I, O> {
        return apply {
            this.fieldProviders[target] = object : ParameterProvider<I, Any?> {
                override fun invoke(data: I): Any? = value
            }
        }
    }

    fun <S : Any> provide(target: KProperty1<S, Any?>, value: Any?): DataClassMapper<I, O> {
        return apply {
            this.fieldProviders[target.name] = object : ParameterProvider<I, Any?> {
                override fun invoke(data: I): Any? = value
            }
        }
    }

    inline fun provide(target: String, crossinline provider: ParameterProvider<I, Any?>): DataClassMapper<I, O> {
        return apply {
            this.fieldProviders[target] = object : ParameterProvider<I, Any?> {
                override fun invoke(data: I): Any? = provider.invoke(data)
            }
        }
    }

    inline fun <S : Any> provide(target: KProperty1<S, Any?>, crossinline provider: ParameterProvider<I, Any?>): DataClassMapper<I, O> {
        return apply {
            this.fieldProviders[target.name] = object : ParameterProvider<I, Any?> {
                override fun invoke(data: I): Any? = provider.invoke(data)
            }
        }
    }

    private fun Any.getParameterValue(path: String): Any? {
        val parameterName = path.substringBefore(".")
        val parametersByName = this.javaClass.kotlin.memberProperties.associateBy { it.name }

        val value = if (parametersByName.containsKey(parameterName))
            parametersByName[parameterName]?.get(this)
        else return null

        return path.replaceFirst(parameterName, "")
            .removePrefix(".")
            .takeIf { it.isNotEmpty() }
            ?.let { value?.getParameterValue(it) } ?: value
    }
}