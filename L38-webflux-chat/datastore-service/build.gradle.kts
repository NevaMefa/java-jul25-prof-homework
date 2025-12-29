dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    implementation("org.postgresql:postgresql:42.7.4")

    implementation("org.flywaydb:flyway-core:10.22.0")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.22.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
}
