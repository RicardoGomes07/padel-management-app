package pt.isel.ls.domain

@JvmInline
value class Name(
    val value: String,
) {
    private fun String.validateName(): Boolean = this.isNotEmpty() && this.length <= 255

    init {
        require(value.validateName()) { "Name is either empty or too long." }
    }
}

fun String.toName() = Name(this)
