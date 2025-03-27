@file:Suppress("ktlint:standard:filename", "ktlint:standard:no-wildcard-imports")

package pt.isel.ls.repository.jdbc

import java.sql.Connection

fun <T> Connection.executeMultipleQueries(block: () -> T): T =
    try {
        transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
        autoCommit = false
        val res = block()
        commit()
        res
    } catch (e: Exception) {
        rollback()
        throw e
    } finally {
        autoCommit = true
        transactionIsolation = Connection.TRANSACTION_READ_COMMITTED
    }
