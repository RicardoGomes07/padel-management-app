package pt.isel.ls.domain

@JvmInline
value class Password(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Password cannot be blank" }
        require(value.length == 44) { "Password must be 44 characters long" }
    }
}

fun String.toPassword(): Password = Password(this)
