package domain.mapper

import domain.model.account.Account
import domain.model.currency.Currency
import domain.model.user.User
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.controller.group.GroupDto
import io.ducket.api.domain.controller.group.GroupMemberAccountPermissionDto
import io.ducket.api.domain.controller.group.GroupMembershipDto
import io.ducket.api.domain.controller.user.dto.UserDto
import io.ducket.api.domain.model.group.Group
import io.ducket.api.domain.model.group.GroupMemberAccountPermission
import io.ducket.api.domain.model.group.GroupMembership
import io.ducket.api.utils.toLocalDate

object GroupMapper {

    fun mapToDto(model: Group): GroupDto {
        val accountMapper = DataClassMapper<Account, AccountDto>()
            .register("currency", DataClassMapper<Currency, CurrencyDto>())

        val userMapper = DataClassMapper<User, UserDto>()
            .register("mainCurrency", DataClassMapper<Currency, CurrencyDto>())
            .provide("sinceDate", model.owner.createdAt.toLocalDate())

        val groupMemberAccountPermissionMapper = DataClassMapper<GroupMemberAccountPermission, GroupMemberAccountPermissionDto>()
            .register("account", accountMapper)

        val groupMembershipMapper = DataClassMapper<GroupMembership, GroupMembershipDto>()
            .register("accountsPermissions", DataClassMapper.collectionMapper(groupMemberAccountPermissionMapper))

        val mapper = DataClassMapper<Group, GroupDto>()
            .register("memberships", DataClassMapper.collectionMapper(groupMembershipMapper))
            .register("owner", userMapper)

        return mapper.invoke(model)
    }
}