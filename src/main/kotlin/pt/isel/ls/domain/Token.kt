package pt.isel.ls.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a token.
 * @property value of the token.
 */
@OptIn(ExperimentalUuidApi::class)
data class Token(
    val value: Uuid,
)

@OptIn(ExperimentalUuidApi::class)
fun generateToken() = Token(Uuid.random())

@OptIn(ExperimentalUuidApi::class)
fun String.toToken() = Token(Uuid.parse(this))
