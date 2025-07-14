plugins {
    `kotlin-dsl`
    alias(libs.plugins.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.serialization.json)
}
