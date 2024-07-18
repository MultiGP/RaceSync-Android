plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("racesync.keystore")
            storePassword = "R@ceSync"
            keyAlias = "racesync"
            keyPassword = "R@ceSync"
        }
    }
    namespace = "com.multigp.racesync"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.multigp.racesync"
        minSdk = 24
        targetSdk = 34
        versionCode = 9
        versionName = "0.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        buildConfigField("String", "API_KEY", "\"da65552b-0de4-331a-04c2-6991bae6fe27\"")
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            buildConfigField(
//                "String",
//                "BASE_URL",
//                "\"https://www.multigp.com/mgp/multigpwebservice/\""
//            )
            buildConfigField("String", "BASE_URL", "\"https://dev.multigp.com/mgp/multigpwebservice/\"")
        }
        debug {
//            buildConfigField(
//                "String",
//                "BASE_URL",
//                "\"https://www.multigp.com/mgp/multigpwebservice/\""
//            )
            buildConfigField("String", "BASE_URL", "\"https://dev.multigp.com/mgp/multigpwebservice/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(mapOf("path" to ":domain")))
    implementation(project(mapOf("path" to ":data")))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.3.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.github.SmartToolFactory:Compose-Colorful-Sliders:1.2.2")
    implementation("androidx.compose.material:material-icons-extended")


    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.accompanist:accompanist-pager:0.25.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.25.0")

    //Permissions and Location
    implementation("com.google.accompanist:accompanist-permissions:0.25.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    //hilt
    val hilt_version = "2.48"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //ROOM DB
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    //Paging
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //GSON
    implementation("com.google.code.gson:gson:2.10")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")

    //Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")

    //Webview
    implementation("com.google.accompanist:accompanist-webview:0.35.0-alpha")

    implementation("com.google.zxing:core:3.5.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

hilt {
    enableAggregatingTask = true
}

//
//kapt {
//    correctErrorTypes = true
//}