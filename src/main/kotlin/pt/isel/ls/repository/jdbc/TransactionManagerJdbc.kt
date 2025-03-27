package pt.isel.ls.repository.jdbc

import pt.isel.ls.repository.Transaction
import pt.isel.ls.repository.TransactionManager
import java.sql.Connection

class TransactionManagerJdbc(
    private val connection: Connection,
) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R = block(TransactionJdbc(connection))
}
