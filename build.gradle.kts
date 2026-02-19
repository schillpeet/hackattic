plugins {
    kotlin("jvm") version "2.3.0"
    application
}

val bouncyCastleVersion = "1.83"
val zxingVersion = "3.5.4"
val djlVersion = "0.36.0"
val ktorVersion = "3.4.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // crypto/SSL
    implementation("org.bouncycastle:bcpkix-jdk15to18:$bouncyCastleVersion")
    implementation("org.bouncycastle:bcprov-jdk15to18:$bouncyCastleVersion")

    // testing
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("io.mockk:mockk:1.14.9")

    // language -> find all country code; the java Locale() isn't fit enough
    implementation("com.neovisionaries:nv-i18n:1.29")

    // to deal with passwort protected zip files
    //implementation("net.lingala.zip4j:zip4j:2.11.5") --> to lame, use john the ripper instead

    // new http client, because java17 http client doesnt support SOCKSv5 proxies
    implementation("com.squareup.okhttp3:okhttp:5.3.2")

    // OCR - for analyzing images
    //implementation("net.sourceforge.tess4j:tess4j:5.18.0")
    implementation("org.openpnp:opencv:4.9.0-0")

    // Cosmetic implementation to make the slf4j error message disappear.
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // reading QR
    implementation("com.google.zxing:javase:$zxingVersion")
    implementation("com.google.zxing:core:$zxingVersion")

    // face detection
    implementation("ai.djl:api:$djlVersion")
    implementation("ai.djl.tensorflow:tensorflow-engine:$djlVersion")
    implementation("ai.djl.tensorflow:tensorflow-model-zoo:$djlVersion")

    // we need our own server that Hackattic can contact.
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")

    // jwt
    implementation("com.auth0:java-jwt:4.5.0")

    // dns
    implementation("dnsjava:dnsjava:3.6.4")

    // redis
    implementation("redis.clients:jedis:7.3.0")
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
