plugins {
    id("java")
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation(libs.flyway)
    implementation(libs.jooqMetaExtensions)
    implementation(libs.postgres)
    implementation(testLibs.testcontainersPostgres)
}
