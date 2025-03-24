package pt.isel.ls.domain

@JvmInline
value class Email(
    val value: String,
) {
    private fun String.validateEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return this.matches(emailRegex)
    }

    init {
        require(value.validateEmail())
    }
}

fun String.toEmail(): Email = Email(this)
