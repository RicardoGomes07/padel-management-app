plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.10"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation(platform("org.http4k:http4k-bom:6.1.0.1"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    // For logging purposes
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.postgresql:postgresql:42.+")
    testImplementation(kotlin("test"))
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

tasks.register<JavaExec>("run") {
    description = "Run the server"
    group = "application"
    mainClass.set("pt.isel.ls.ServerKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

tasks.register<Exec>("BuildDataBase") {
    psql ?: error("psql not found. Unable to run database commands.")
    environment("PGPASSWORD", "postgres")
    commandLine(
        psql,
        "-U",
        "postgres",
        "-f",
        createTablesScript,
    )
}

tasks.register<Exec>("InsertValuesIntoDb") {
    psql ?: error("psql not found. Unable to run database commands.")
    environment("PGPASSWORD", "postgres")
    commandLine(
        psql,
        "-U",
        "postgres",
        "-f",
        insertValuesScript,
    )
}

val psql =
    System
        .getenv("Path")
        .split(";")
        .find { it.contains("PostgreSQL") }
        ?.let { "$it\\psql.exe" }

val createTablesScript =
    project
        .layout
        .projectDirectory
        .dir("src/test/sql")
        .file("createSchema.sql")
        .toString()
val postgresPswd = "postgres"

val insertValuesScript =
    project
        .layout
        .projectDirectory
        .dir("src/test/sql")
        .file("addData.sql")
        .toString()

task<Exec>("dropTestsDb") {
    psql ?: error("psql not found. Unable to run database commands.")
    environment("PGPASSWORD", postgresPswd)
    commandLine(psql, "-U", "postgres", "-c", "DROP DATABASE IF EXISTS tests_db;")
}

task<Exec>("createTestsDb") {
    psql ?: error("psql not found. Unable to run database commands.")
    dependsOn("dropTestsDb")
    environment("PGPASSWORD", postgresPswd)
    commandLine(psql, "-U", "postgres", "-c", "CREATE DATABASE tests_db;")
}

task<Exec>("createTablesTestsDb") {
    psql ?: error("psql not found. Unable to run database commands.")
    environment("PGPASSWORD", postgresPswd) // Needed for psql authentication
    dependsOn("createTestsDb")
    commandLine(
        psql,
        "-U",
        "postgres",
        "-d",
        "tests_db",
        "-f",
        createTablesScript,
    )
}

task<Exec>("deleteTestsDb") {
    psql ?: error("psql not found. Unable to run database commands.")
    environment("PGPASSWORD", postgresPswd) // Needed for psql authentication
    commandLine(
        psql,
        "-U",
        "postgres",
        "-c",
        "DROP DATABASE tests_db;",
    )
}

tasks.named<Test>("test") {
    if (psql != null) {
        environment("DB_URL", "jdbc:postgresql://localhost:5432/tests_db?user=postgres&password=postgres")
        dependsOn("createTablesTestsDb")
        finalizedBy("deleteTestsDb")
    }
}
