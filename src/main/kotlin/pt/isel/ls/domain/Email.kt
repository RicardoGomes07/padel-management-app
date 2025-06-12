package pt.isel.ls.domain

@JvmInline
value class Email(
    val value: String,
) {
    private fun String.validateEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return this.length <= 255 && this.matches(emailRegex)
    }

    init {
        require(value.validateEmail()) { "Email is either misformatted or is too long." }
    }
}

fun String.toEmail(): Email = Email(this)
