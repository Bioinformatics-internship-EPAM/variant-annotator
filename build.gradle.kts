plugins {
    java
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "3.1.1"
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
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

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.testcontainers:testcontainers:1.15.2")
    testImplementation("org.testcontainers:postgresql:1.15.2")
    testImplementation("org.testcontainers:junit-jupiter:1.15.2")

    val lombok = "org.projectlombok:lombok:1.18.18"
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)
}

sonarqube {
    properties {
        property("sonar.projectName", "variant-annotator")
        property("sonar.projectKey", "Bioinformatics-internship-EPAM_variant-annotator")
        property("sonar.host.url", "https://sonarcloud.io")
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
