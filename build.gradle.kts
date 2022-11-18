import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.5.31"

    id("org.jetbrains.compose") version "1.0.0-beta5"
}
group = "szewek.craftery"

val asmVersion = "9.2"
val jacksonVersion = "2.13.0"
val composeVersion = "1.3.0-beta02"
val desktoposeVersion = "0.3.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven { url = uri("https://jitpack.io") }
    google()
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))

compose.desktop {
    application {
        mainClass = "szeweq.craftery.Craftery"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "craftery"
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.github.szeweq.desktopose:core:$desktoposeVersion")
    implementation("com.github.szeweq.desktopose:hover:$desktoposeVersion")
    implementation("com.github.szeweq.desktopose:combo-box:$desktoposeVersion")
    implementation("com.github.szeweq.desktopose:progress:$desktoposeVersion")

    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:$composeVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    implementation("com.electronwill.night-config:toml:3.6.4")
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-tree:$asmVersion")
    implementation("org.ow2.asm:asm-analysis:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.ow2.asm:asm-util:$asmVersion")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xlambdas=indy")
}
