plugins {
    id("java")
}

dependencies {
    implementation(project(":jooq-testcontainers-generation"))
    runtimeOnly(project(":schema"))
    implementation(libs.flyway)
    implementation(libs.jooq)
    implementation(libs.postgres)
    testImplementation(testLibs.bundles.testing)
    testImplementation(testLibs.bundles.testcontainersPostgres)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
