plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
        }
    }
}
