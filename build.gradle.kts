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
}

tasks.register<Exec>("BuildDataBase") {
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
        ?: throw Exception("Missing PostgreSQL environment variable")

val createTablesScript =
    project
        .layout
        .projectDirectory
        .dir("src/test/sql")
        .file("createSchema.sql")
        .toString()

val insertValuesScript =
    project
        .layout
        .projectDirectory
        .dir("src/test/sql")
        .file("addData.sql")
        .toString()

task<Exec>("createTestsDb") {
    environment("PGPASSWORD", "postgres") // Needed for psql authentication
    commandLine(
        psql,
        "-U",
        "postgres",
        "-c",
        "CREATE DATABASE tests_db;",
    )
}
task<Exec>("createTablesTestsDb") {
    environment("PGPASSWORD", "postgres") // Needed for psql authentication
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
    environment("PGPASSWORD", "postgres") // Needed for psql authentication
    commandLine(
        psql,
        "-U",
        "postgres",
        "-c",
        "DROP DATABASE tests_db;",
    )
}

tasks.named<Test>("test") {
    environment("DB_URL", "jdbc:postgresql://localhost:5432/tests_db?user=postgres&password=postgres")
    dependsOn("createTablesTestsDb")
    finalizedBy("deleteTestsDb")
}
