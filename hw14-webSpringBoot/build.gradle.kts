plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway") version "9.22.3"
}

group = "ru.otus"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    // База данных
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // Утилиты и логирование
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("ch.qos.logback:logback-classic")

    // Тестирование
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
}

flyway {
    url = "jdbc:postgresql://localhost:5431/demoDB"
    user = "usr"
    password = "pwd"
    locations = arrayOf("classpath:db/migration")
}

tasks.test {
    useJUnitPlatform()
}

tasks.matching { it.name.startsWith("sonarlint", ignoreCase = true) }
    .configureEach { enabled = false }