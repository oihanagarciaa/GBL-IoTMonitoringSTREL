group = "at.ac.tuwien.trustcps"
version = "1.0-SNAPSHOT"

plugins {
    java
    jacoco
    kotlin("jvm") version "1.6.21"
    id("org.sonarqube") version "3.3"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    // Needed by pattern matching on switches:
    options.compilerArgs.add("--enable-preview")
}


sonarqube {
    properties {
        property("sonar.projectKey", "oihanagarciaa_GBL-IoTMonitoringSTREL")
        property("sonar.organization", "oihanagarciaa")
        property("sonar.host.url", "https://sonarcloud.io")
    }
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

}

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
