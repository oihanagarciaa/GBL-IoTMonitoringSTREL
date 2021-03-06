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

// Sonarqube settings & task

sonarqube {
    properties {
        property("sonar.projectKey", "oihanagarciaa_GBL-IoTMonitoringSTREL")
        property("sonar.organization", "oihanagarciaa")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}


tasks.register("analyze") {
    dependsOn(tasks.named("check"))
    dependsOn(tasks.named("sonarqube"))
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
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.6.21")

    implementation ("commons-io:commons-io:2.6")

    implementation("org.powermock:powermock-module-junit4:1.6.4")
    implementation("org.powermock:powermock-api-mockito2:2.0.9")
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

tasks.jar {
    archiveFileName.set("moonlightMiddleware.jar")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes(mapOf("Main-Class" to "main.Main"))
    }
    doFirst {
        from({
            configurations.compileClasspath.get().map {
                if (it.isDirectory) it else zipTree(it)
            }
        })
    }

    exclude ("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}