import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.airwallex.grpc-spring") version "1.1.5-SNAPSHOT"
}

group = "demo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://artistry.airwallex.com/repository/lib-release/libs-release-local")
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

grpc {
    reactorInterface()                      // By default generates coroutines interface
    serviceInterfaceNameSuffix("Api") // Default value is "Rpc"
}