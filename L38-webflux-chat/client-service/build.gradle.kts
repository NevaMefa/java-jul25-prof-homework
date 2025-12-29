dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    implementation("org.webjars:webjars-locator-core:0.52")
    implementation("org.webjars:sockjs-client:1.5.1")
    implementation("org.webjars:stomp-websocket:2.3.4")
    implementation("org.webjars:bootstrap:5.3.3")
}
