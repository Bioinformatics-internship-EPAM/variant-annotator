import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    java
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "3.1.1"
    id("jacoco")
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_15
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:7.5.4")
    implementation("org.postgresql:postgresql")
    implementation(group = "com.github.samtools", name = "htsjdk", version = "2.24.0")
    implementation("com.vladmihalcea:hibernate-types-52:2.10.3")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.springframework.security:spring-security-test")
    implementation ("javax.validation:validation-api")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.testcontainers:testcontainers:1.15.2")
    testImplementation("org.testcontainers:postgresql:1.15.2")
    testImplementation("org.testcontainers:junit-jupiter:1.15.2")

    compile("javax.json:javax.json-api:1.0")

    val lombok = "org.projectlombok:lombok:1.18.18"
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)
}

jacoco {
    toolVersion = "0.8.7"
}

tasks {
    jacocoTestReport {
        dependsOn(test)

        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        subprojects.forEach {
            sourceSets(it.sourceSets["main"])
        }

        reports {
            listOf(xml, html).forEach { report ->
                report.isEnabled = true
                report.destination = file("$buildDir/reports/jacoco/coverage.${report.name}")
            }
        }
    }

    test {
        useJUnitPlatform()

        testLogging {
            events(PASSED, SKIPPED, FAILED)
        }

        finalizedBy(jacocoTestReport)
    }

    sonarqube {
        properties {
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.organization", "bioinformatics-internship")
            property("sonar.projectKey", "Bioinformatics-internship-EPAM_variant-annotator")
            property("sonar.projectName", "variant-annotator")
            property("sonar.tests", "src/test")
            property("sonar.binaries", "${project.buildDir}/classes")
            property("sonar.sources", "src/main")
            property("sonar.language", "java")
            property("sonar.junit.reportPaths", "${project.buildDir}/test-results")
            property("sonar.dynamicAnalysis", "reuseReports")
            property("sonar.java.coveragePlugin", "jacoco")
            property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/reports/jacoco/coverage.xml")
        }
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}
