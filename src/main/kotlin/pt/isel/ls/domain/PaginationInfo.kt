package pt.isel.ls.domain

data class PaginationInfo<T>(
    val items: List<T>,
    val count: Int,
)
