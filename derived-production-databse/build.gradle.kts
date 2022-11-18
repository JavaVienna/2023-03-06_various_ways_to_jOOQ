plugins {
    id("java")
}

dependencies {
    implementation(project(":schema"))
    implementation(libs.flyway)
    implementation(libs.postgres)
    implementation(testLibs.testcontainersPostgres)
    testImplementation(project(":jooq-testcontainers-generation"))
    testImplementation(libs.jooq)
    testImplementation(testLibs.bundles.testing)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
