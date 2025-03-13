@file:OptIn(ExperimentalUuidApi::class)

package pt.isel.ls.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a user.
 * @property uid Unique identifier of the user.
 * @property name Name of the user.
 * @property email Unique email of the user.
 */
data class User(
    val uid: UInt,
    val name: Name,
    val email: Email,
    val token: Uuid,
)
