group = "at.softwarecraftsmen"
version = "1.0-SNAPSHOT"

project.subprojects {
    version = rootProject.version
    repositories {
        mavenCentral()
    }
}
