dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.ehcache:ehcache")
    implementation ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")

    implementation("org.hibernate.orm:hibernate-core")
    implementation("org.flywaydb:flyway-core")
    implementation ("org.flywaydb:flyway-database-postgresql")


    implementation("org.postgresql:postgresql")
}
