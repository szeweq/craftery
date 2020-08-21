import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    java
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
}
group = "szewek.mctool"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.4.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    applicationDefaultJvmArgs = mutableListOf("--add-opens", "javafx.controls/javafx.scene.control=ALL-UNNAMED")
    mainClassName = "szewek.mctool.Launcher"
}

javafx {
    version = "11.0.2"
    modules("javafx.controls", "javafx.graphics")
}
repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("org.ow2.asm:asm:9.0-beta")
    implementation("no.tornado:tornadofx:1.7.20")
    testImplementation(kotlin("test-junit"))

    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:win")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:linux")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:mac")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "szewek.mctool.Launcher"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it).matching {
            exclude("module-info*")
            exclude("META-INF/maven/**")
            exclude("META-INF/proguard/**")
            exclude("META-INF/com.android.tools/**")
            exclude("META-INF/LICENSE*")
            exclude("META-INF/NOTICE*")
            exclude("META-INF/DEPENDENCIES*")
        }
    })
}