package pt.isel.ls.domain

/**
 * Represents a user.
 * @property uid Unique identifier of the user.
 * @property name Name of the user.
 * @property email Unique email of the user.
 */
data class User(
    val uid: Int,
    val name: String,
    val email: String,
) {
    private fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    init {
        require(validateEmail(email))
    }
}
