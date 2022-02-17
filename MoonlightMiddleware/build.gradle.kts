plugins {
    java
    jacoco
    id("org.sonarqube") version "3.3"
}

group 'at.ac.tuwien.trustcps'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/moonlight.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")


}

test {
    useJUnitPlatform()
}