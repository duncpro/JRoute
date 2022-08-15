plugins {
    java
    jacoco
    `maven-publish`
}

group = "com.duncpro"
version = "1.0-SNAPSHOT-11"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("net.jcip:jcip-annotations:1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

val jacocoTestReport by tasks.getting(JacocoReport::class) {
    classDirectories.setFrom(sourceSets.main.get().output)
    sourceDirectories.setFrom(sourceSets.main.get().allSource.srcDirs)
    executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
    sourceSets {
        add(main.get())
    }
}

tasks.check {
    finalizedBy(jacocoTestReport)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://duncpro-personal-618824625980.d.codeartifact.us-east-1.amazonaws.com/maven/duncpro-personal/")
            credentials {
                username = "aws"
                password = System.getenv("CODEARTIFACT_AUTH_TOKEN")
            }
        }
    }
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11
