package pt.isel.ls.repository.mem

import pt.isel.ls.repository.Transaction
import pt.isel.ls.repository.TransactionManager

class TransactionManagerInMem : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R = block(TransactionInMem())
}
