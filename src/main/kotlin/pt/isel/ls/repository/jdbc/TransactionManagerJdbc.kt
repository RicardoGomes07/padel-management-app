package pt.isel.ls.repository.jdbc

import pt.isel.ls.repository.Transaction
import pt.isel.ls.repository.TransactionManager
import javax.sql.DataSource

class TransactionManagerJdbc(
    private val dataSource: DataSource,
) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R = block(TransactionJdbc(dataSource))
}
