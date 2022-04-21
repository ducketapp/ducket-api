package io.ducket.api.domain.controller.rule

import io.ducket.api.app.ImportRuleLookupType
import io.ducket.api.plugins.InvalidDataException
import io.ducket.api.utils.declaredMemberPropertiesNull
import org.valiktor.functions.*

data class ImportRuleUpdateDto(
    val name: String?,
    val lookupType: ImportRuleLookupType?,
    val keywords: List<String>?,
    val categoryId: Long?,
) {
    fun validate(): ImportRuleUpdateDto {
        org.valiktor.validate(this) {
            validate(ImportRuleUpdateDto::name).isNotNull().hasSize(1, 64)
            validate(ImportRuleUpdateDto::lookupType).isNotNull()
            validate(ImportRuleUpdateDto::keywords).isNotNull().isNotEmpty()
            validate(ImportRuleUpdateDto::categoryId).isNotNull()

            if (this@ImportRuleUpdateDto.declaredMemberPropertiesNull()) throw InvalidDataException()
        }
        return this
    }
}
