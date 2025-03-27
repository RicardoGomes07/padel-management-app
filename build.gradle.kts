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

tasks.named<Test>("test") {
    environment("DB_URL", "jdbc:postgresql://localhost:5439/db?user=dbuser&password=changeit")
    dependsOn("dbTestsWait")
    finalizedBy("dbTestsDown")
}

val dockerDir: Directory = project.layout.projectDirectory.dir("src/test/docker/")
val dockerComposePath = dockerDir.file("docker-compose.yml").toString()

task<Exec>("dbTestsUp") {
    commandLine(
        "docker",
        "compose",
        "-p",
        "padel-courts",
        "-f",
        dockerComposePath,
        "up",
        "-d",
        "--build",
        "--force-recreate",
        "padel-courts-db",
    )
}

task<Exec>("dbTestsWait") {
    commandLine("docker", "exec", "padel-courts-db", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
    commandLine("docker", "compose", "-p", "padel-courts", "-f", dockerComposePath, "down", "padel-courts-db")
}
