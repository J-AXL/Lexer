plugins {
    id("java")
}

group = "axl.lexer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.fusesource.jansi:jansi:2.4.0")
}

tasks.test {
    useJUnitPlatform()
}