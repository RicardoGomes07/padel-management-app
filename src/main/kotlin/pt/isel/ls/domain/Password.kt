package pt.isel.ls.domain

@JvmInline
value class Password(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Password cannot be blank" }
        require(value.length == 32) { "Password must be 32 characters long" }
    }
}

fun String.toPassword(): Password = Password(this)
