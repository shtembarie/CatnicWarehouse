package com.example.shared.repository.login.model

data class RootResponse(
    val data: DataResponse? = null,
    val exceptionInfo: ExceptionInfo? = null,
    val metaData: MetaData? = null
)

data class DataResponse(
    val userDTO: UserDTO? = null,
    val siteMapDTO: SiteMapDTO? = null
)

data class UserDTO(
    val email: String? = null,
    val fullName: String? = null,
    val initials: String? = null,
    val rights: List<String>? = null
)

data class SiteMapDTO(
    val siteMapItemCollection: List<SiteMapItemCollection>? = null
)

data class SiteMapItemCollection(
    val siteMapItem: SiteMapItem? = null,
    val subItems: List<SiteMapItemCollection>? = null
)

data class SiteMapItem(
    val text: String? = null,
    val url: String? = null,
    val cssClass: String? = null,
    val key: String? = null
)

data class ExceptionInfo(
    val errorCode: String? = null,
    val errorMessage: String? = null
)

data class MetaData(
    val paginationInfo: PaginationInfo? = null,
    val fieldsCollection: FieldsCollection? = null,
    val isModified: Boolean? = null,
    val forceSave: Boolean? = null,
    val projectVersion: String? = null,
    val validationInfos: List<Any>? = null
)

data class PaginationInfo(
    val offset: Int? = null,
    val limit: Int? = null,
    val total: Int? = null
)

data class FieldsCollection(
    val fieldsCollection: List<Any>? = null,
    val staticFiltersCollection: Any? = null,
    val filters: List<Any>? = null
)
