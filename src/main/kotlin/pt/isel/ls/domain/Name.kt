package pt.isel.ls.domain

@JvmInline
value class Name(
    val value: String,
) {
    private fun String.validateName(): Boolean = this.isNotEmpty()

    init {
        require(value.validateName())
    }
}

fun String.toName() = Name(this)
