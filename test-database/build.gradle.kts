plugins {
    id("java")
}

dependencies {
    implementation(project(":jooq-testcontainers-generation"))
    implementation("net.datafaker:datafaker:1.8.0")
    implementation(libs.flyway)
    implementation(libs.jooq)
    implementation(libs.postgres)
    implementation(testLibs.testcontainersPostgres)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
