import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.4.31"

    id("org.jetbrains.compose") version "0.3.0"
}
group = "szewek.craftery"

val ktorVersion = "1.4.0"
val fuelVersion = "2.3.1"
val asmVersion = "9.0"

//val compileKotlin: KotlinCompile by tasks
//val compileJava: JavaCompile by tasks
//compileJava.destinationDir = compileKotlin.destinationDir

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

compose.desktop {
    application {
        mainClass = "szewek.craftery.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "craftery"
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)

    // Two JSON libraries
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("javax.json:javax.json-api:1.1.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
    implementation("com.github.kittinunf.result:result:3.1.0")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    implementation("com.electronwill.night-config:toml:3.6.3")
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-tree:$asmVersion")
    implementation("org.ow2.asm:asm-analysis:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.ow2.asm:asm-util:$asmVersion")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
    kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
}

/* jlink {
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "mctool"
        jvmArgs.addAll(application.applicationDefaultJvmArgs)
        jvmArgs.addAll(listOf(
                "--add-opens", "javafx.controls/javafx.scene.control=szewek.mctool.merged.module",
                "--add-opens", "javafx.graphics/javafx.scene=szewek.mctool.merged.module",
                "--add-exports", "javafx.graphics/com.sun.javafx.tk=szewek.mctool.merged.module"
        ))
        noConsole = true
    }
    addExtraDependencies("javafx")
    imageZip.set(project.file("${project.buildDir}/image-zip/mctool-img.zip"))
} */