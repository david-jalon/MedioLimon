import java.util.Properties
import java.io.FileInputStream

// 1. Cargar el archivo local.properties
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(FileInputStream(localPropertiesFile))
}

// 2. Extraer las claves API
val spoonacularApiKey = properties.getProperty("SPOONACULAR_API_KEY") ?: ""
val geminiApiKey = properties.getProperty("GEMINI_API_KEY") ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.mushi.mediolimon"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mushi.mediolimon"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyecta las claves como variables String en la clase BuildConfig
        buildConfigField("String", "SPOONACULAR_API_KEY", "\"$spoonacularApiKey\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // Habilita la generación de la clase BuildConfig
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Fragments
    implementation(libs.androidx.navigation.fragment.ktx) // Para la navegación con Fragments
    implementation(libs.androidx.navigation.ui.ktx)

    // Dependencias de test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    val room_version = "2.8.3"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit (Cliente HTTP)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Convertidor para transformar JSON a objetos Kotlin/Java
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide (para cargar imágenes desde URLs)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // SDK de IA de Google (Gemini) - Usando una versión anterior para probar compatibilidad.
    implementation("com.google.ai.client.generativeai:generativeai:0.5.0")
}