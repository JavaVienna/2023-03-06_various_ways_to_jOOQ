plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation(project(":jooq-flyway-generation"))
    implementation(project(":schema"))
    implementation(libs.flyway)
    implementation(libs.jakartaPersistence)
    implementation(libs.jooq)
    implementation(libs.postgres)
    implementation(libs.springDataJpa)
    implementation(libs.springValidation)
    testImplementation(testLibs.bundles.testing)
    testImplementation(testLibs.springDataTest)
    testImplementation(testLibs.testcontainersPostgres)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
