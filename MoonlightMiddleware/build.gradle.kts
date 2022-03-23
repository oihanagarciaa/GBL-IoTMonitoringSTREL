group = "at.ac.tuwien.trustcps"
version = "1.0-SNAPSHOT"

plugins {
    java
    jacoco
    id("org.sonarqube") version "3.3"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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