package pt.isel.ls.domain

@JvmInline
value class Password(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Password cannot be blank" }
        require(value.length >= 6) { "Password must be at least 6 characters long" }
        require(value.length <= 64) { "Password must not exceed 64 characters" }
    }

    override fun toString(): String = value
}

fun String.toPassword(): Password = Password(this)
