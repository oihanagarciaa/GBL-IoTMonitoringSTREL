// Project constants

group = "at.ac.tuwien.trustcps"
version = "1.0-SNAPSHOT"
val javaVersion = JavaVersion.VERSION_17

// Gradle plugins required to run the software

plugins {
    java
    jacoco
    kotlin("jvm") version "1.6.21"
    id("org.sonarqube") version "3.3"
}

// Java & Kotlin JVM version settings

tasks.withType<JavaCompile> {
    // Needed by pattern matching on switches:
    options.compilerArgs.add("--enable-preview")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

// Dependencies & repositories from which they are fetched

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/moonlight.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:4.3.1")


    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.8.5")
    implementation(kotlin("script-runtime"))

}

// JUnit & JaCoCo settings

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
    }
}

// Sonarqube settings

sonarqube {
    properties {
        property("sonar.projectKey", "oihanagarciaa_GBL-IoTMonitoringSTREL")
        property("sonar.organization", "oihanagarciaa")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
