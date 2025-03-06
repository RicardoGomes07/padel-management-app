package pt.isel.ls

import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import java.sql.ResultSet
import kotlin.test.assertTrue

class DBTests {
    private val dataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")

    init {
        dataSource.setURL(jdbcDatabaseURL)
    }

    @Test
    fun create_new_student_and_delete_it() {
        dataSource.connection.use {
            val oldStudents = it.prepareStatement("select * from students").executeQuery()
            println("Initial Students:")
            displayStudents(oldStudents)
            val stm = it.prepareStatement("insert into students values (1, 'João', 1)").executeUpdate()
            assertTrue(stm == 1)
            val newStudents = it.prepareStatement("select * from students").executeQuery()
            println("Students after insert:")
            displayStudents(newStudents)
            val stm2 = it.prepareStatement("delete from students where name = 'João'").executeUpdate()
            assertTrue(stm2 == 1)
            val students = it.prepareStatement("select * from students").executeQuery()
            println("Students after delete:")
            displayStudents(students)
        }
    }

    private fun displayStudents(rs: ResultSet){
        println("/*************/")
        while (rs.next()){
            print(rs.getString("number") + " ")
            print(rs.getString("name") + " ")
            print(rs.getString("course") + " "+ "\n")
        }
        println("/*************/")
    }

}