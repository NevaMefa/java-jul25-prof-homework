plugins {
    java
    id("com.google.protobuf") version "0.9.4"
    application
}

group = "ru.otus.numbers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val grpcVersion = "1.52.1"
val protobufVersion = "3.21.12"

dependencies {
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    implementation("org.slf4j:slf4j-simple:2.0.7")

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("ru.otus.numbers.server.NumbersServer")
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Запускает gRPC сервер"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ru.otus.numbers.server.NumbersServer")
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Запускает gRPC клиент"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ru.otus.numbers.client.NumbersClient")
}

configurations.all {
    resolutionStrategy {
        force("com.google.errorprone:error_prone_annotations:2.27.0")
        force("com.google.guava:guava:32.1.3-android")
        force("com.google.code.gson:gson:2.10.1")
        force("com.google.protobuf:protobuf-java:3.25.3")
    }
}