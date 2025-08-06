package pt.isel.ls.repository

interface TransactionManager {
    fun <R> run(block: Transaction.() -> R): R
}
