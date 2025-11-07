plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.veterinaria" // Tu paquete base
    compileSdk = 36// Usamos 34, 35 puede ser preview

    defaultConfig {
        applicationId = "com.example.veterinaria"
        minSdk = 24
        targetSdk = 36 // Usamos 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Como el profe
        targetCompatibility = JavaVersion.VERSION_11 // Como el profe
    }
    kotlinOptions {
        jvmTarget = "11" // Como el profe
    }
    buildFeatures {
        viewBinding = false // Usamos findViewById como el profe
    }
}

dependencies {

    // Dependencias base del profe
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.0") // activity-ktx es mejor que activity
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // --- Dependencias de Red (iguales a las del profe) ---
    // Núcleo de Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Convertidor JSON Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    // Interceptor de OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Coroutines Soporte (usamos 1.8.0, es casi igual a 1.8.1)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    // Extensiones KTX de Lifecycle (lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    // Adaptadores de Moshi para Kotlin
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")


    // --- Dependencias de Cámara (iguales a las del profe) ---
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")


}