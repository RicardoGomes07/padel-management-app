package pt.isel.ls

import junit.framework.TestCase
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

    @Test
    fun create_new_table_classroom_insert_tuple_update_same_tuple_then_delete_it(){
        dataSource.connection.use {
            val stmt = it.createStatement()

            val deletePreviousCMD = "drop table if exists classroom"

            stmt.executeUpdate(deletePreviousCMD)

            val createClassroomCMD = "create table classroom (\n" +
                    "  classroom_id serial primary key,\n" +
                    "  name varchar(80),\n" +
                    "  num_of_students integer" +
                    ");"

            val res1 = stmt.executeUpdate(createClassroomCMD)

            assertTrue(res1 == 0)

            val fillClassroomCMD = "insert into classroom(name, num_of_students) values ('42D', 30), ('41N', 15)"

            val res2 = stmt.executeUpdate(fillClassroomCMD)

            assertTrue(res2 == 2)

            val updateClassroomNumOfStudentsCMD = "update classroom set num_of_students = 35 where classroom_id = 1"

            val res3 = stmt.executeUpdate(updateClassroomNumOfStudentsCMD)

            assertTrue(res3 == 1)

            val deleteClassroomCMD = "delete from classroom where classroom_id = 1"

            val res4 = stmt.executeUpdate(deleteClassroomCMD)

            assertTrue(res4 == 1)
        }
    }

    @Test
    fun create_new_course_and_update_the_courses() {
        dataSource.connection.use { connection ->
            val oldStudents = connection.prepareStatement("SELECT * FROM students").executeQuery()
            displayStudents(oldStudents)
            val changedStudent = connection.prepareStatement("UPDATE students set name='Ricardo' where name='Bob'").executeUpdate()
            TestCase.assertTrue(changedStudent == 1)
            val newStudents = connection.prepareStatement("SELECT * FROM students").executeQuery()
            displayStudents(newStudents)
        }

    }

}