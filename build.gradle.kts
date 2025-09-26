import net.minecraftforge.gradle.common.util.RunConfig
import org.gradle.kotlin.dsl.implementation
import java.time.Instant
import java.time.format.DateTimeFormatter

fun property(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
    id("idea")
    id("maven-publish")
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

repositories {
    maven("https://maven.tterrag.com")
    maven("https://mvnrepository.com/artifact/com.tterrag.registrate/Registrate")
    maven {
        name = "Configuration"
        url= uri("https://api.repsy.io/mvn/toma/public/")
    }
    maven {
        name = "firstdarkdev"
        url = uri("https://maven.firstdarkdev.xyz/snapshots")
    }
    maven {
        name = "GTCEu Maven"
        url = uri("https://maven.gtceu.com")
        content {
            includeGroup("com.gregtechceu.gtceu")
        }
    }
    maven("https://harleyoconnor.com/maven")
    maven("https://maven.parchmentmc.org")
    mavenCentral()
}

val modName = property("mod_name")
val modId = property("mod_id")
val modVersion = property("mod_version")
val mcVersion = property("minecraft_version")
val authorName = property("mod_authors")

val gtceuVersion = property("gtceu_version")
val version = "$mcVersion-$modVersion"
val group = property("mod_group_id")
val ldlibVersion = property("ldlib_version")

minecraft {
    mappings("parchment", "${property("mapping_version")}-$mcVersion")

    runs {
        create("client") {
            applyDefaultConfiguration()

            if (project.hasProperty("mcUuid")) {
                args("--uuid", property("mcUuid"))
            }
            if (project.hasProperty("mcUsername")) {
                args("--username", property("mcUsername"))
            }
            if (project.hasProperty("mcAccessToken")) {
                args("--accessToken", property("mcAccessToken"))
            }
        }

        create("server") {
            applyDefaultConfiguration("run-server")
        }

        create("data") {
            applyDefaultConfiguration()

            args(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/"),
                "--existing", file("src/main/resources"),
                "--existing-mod", "dynamictrees",
                "--existing-mod", "gtceu",
                "--existing-mod", "rubberdt"
            )
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })
    //libs
    minecraft("net.minecraftforge:forge:$mcVersion-${property("forge_version")}")

    //DynamicTrees
    implementation("com.ferreusveritas.dynamictrees:DynamicTrees-$mcVersion:${property("dynamic_trees_version")}")

    //GregTech CEu Modern
    implementation("com.gregtechceu.gtceu:gtceu-${mcVersion}:${gtceuVersion}")
    implementation("com.lowdragmc.ldlib:ldlib-forge-${mcVersion}:${ldlibVersion}")

    //DynmaicTrees Tools/Utilities
    runtimeOnly(fg.deobf("curse.maven:jade-324717:5072729"))
    runtimeOnly(fg.deobf("curse.maven:jei-238222:5101366"))
    runtimeOnly(fg.deobf("curse.maven:cc-tweaked-282001:5118388"))
    runtimeOnly(fg.deobf("curse.maven:suggestion-provider-fix-469647:4591193"))

}

tasks.named<Jar>("jar") {
    manifest.attributes(
        "Specification-Title" to project.name,
        "Specification-Vendor" to authorName,
        "Specification-Version" to "1",
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version,
        "Implementation-Vendor" to authorName,
        "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    )

    archiveBaseName.set(modName)
    archiveVersion.set("${modVersion}-${mcVersion}")

    finalizedBy("reobfJar")
}

tasks.build {
    dependsOn("reobfJar")
}
java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven("file:///${project.projectDir}/mcmodsrepo")
    }
}

fun RunConfig.applyDefaultConfiguration(runDirectory: String = "run") {
    workingDirectory = file(runDirectory).absolutePath

    property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
    property("forge.logging.console.level", "debug")

    property("mixin.env.remapRefMap", "true")
    property("mixin.env.refMapRemappingFile", "${layout.buildDirectory}/createSrgToMcp/output.srg")

    mods {
        create(modId) {
            source(sourceSets.main.get())
        }
    }
}