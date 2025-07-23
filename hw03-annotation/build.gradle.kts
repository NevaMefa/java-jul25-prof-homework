dependencies {
    implementation ("ch.qos.logback:logback-classic")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("org.assertj:assertj-core")
}
tasks.named("sonarlintMain").configure {
    enabled = false
}

tasks.named("spotlessApply").configure {
    enabled = false
}
