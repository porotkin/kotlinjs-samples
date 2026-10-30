plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting

        val clientCommonMain by creating {
            dependsOn(commonMain)
        }

        val mobileCommon by creating {
            dependsOn(clientCommonMain)
        }

        val webMain by creating {
            dependsOn(clientCommonMain)
        }

        val jsMain by getting {
            dependsOn(webMain)
        }

        val jvmMain by getting {
            dependsOn(mobileCommon)
            dependencies {
                implementation(libs.slf4j.api)
            }
        }
    }
}

if (providers.systemProperty("build.mobile.enabled").orNull == "true") {
    apply(plugin = "android-library-conventions")
    kotlin {
        val mobileCommon = sourceSets.getByName("mobileCommon")
        sourceSets.getByName("androidMain") {
            dependsOn(mobileCommon)
            dependencies {
                implementation(libs.ktor.client.logging)
            }
        }
    }
}
