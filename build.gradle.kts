plugins {
    java
    jacoco
}

group = "com.duncpro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("net.jcip:jcip-annotations:1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
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
