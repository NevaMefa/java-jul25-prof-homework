plugins {
    application
}

application {
    mainClass.set("ru.otus.HomeWorkk")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP")
}