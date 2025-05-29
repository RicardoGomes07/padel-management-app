package pt.isel.ls.domain

@JvmInline
value class Password(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Password cannot be blank" }
        require(value.length >= 8) { "Password must be at least 8 characters long" }
        require(value.length <= 32) { "Password must not exceed 32 characters" }
    }

    override fun toString(): String = value
}

fun String.toPassword(): Password = Password(this)
