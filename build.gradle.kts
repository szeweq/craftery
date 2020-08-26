import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    application
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("org.beryx.jlink") version "2.21.3"
}
group = "szewek.mctool"

val ktorVersion = "1.4.0"
val fuelVersion = "2.2.3"

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDir = compileKotlin.destinationDir

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    applicationDefaultJvmArgs = listOf(
            "--add-opens", "javafx.controls/javafx.scene.control=tornadofx",
            "--add-opens", "javafx.graphics/javafx.scene=tornadofx"
    )
    mainClassName = "szewek.mctool.Launcher"
    mainModule.set("mctool.main")
}

javafx {
    version = "11.0.2"
    modules("javafx.controls")
}
repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("com.github.kittinunf.result:result:3.1.0")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    implementation("org.ow2.asm:asm:9.0-beta")
    implementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

jlink {
    launcher {
        name = "mctool"
        jvmArgs.addAll(application.applicationDefaultJvmArgs)
        jvmArgs.addAll(listOf(
                "--add-opens", "javafx.controls/javafx.scene.control=szewek.mctool.merged.module",
                "--add-opens", "javafx.graphics/javafx.scene=szewek.mctool.merged.module"
        ))
        noConsole = true
    }
    addExtraDependencies("javafx")
    imageZip.set(project.file("${project.buildDir}/image-zip/mctool-image.zip"))
}
