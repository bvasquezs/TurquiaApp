package cl.duoc.turquiaapp.domain.model

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val childName: String,
    val childAge: Int,
    val photoPath: String? = null
)
