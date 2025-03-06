package pt.isel.ls

import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
class DBTests {
    private val dataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")

    @Test
    fun create_new_student_and_delete_old_sudent() {

    }

}