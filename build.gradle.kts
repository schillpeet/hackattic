plugins {
    kotlin("jvm") version "2.1.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.+")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // crypto
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.83")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.83")

    // testing
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")

    // language -> find all country code; the java Locale() isn't fit enough
    implementation("com.neovisionaries:nv-i18n:1.29")
}
tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("hackattic.MainKt")
}

kotlin {
    jvmToolchain(17)
}
