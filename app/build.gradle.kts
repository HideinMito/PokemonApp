plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services") version "4.3.15" apply false
}

android {
    namespace = "com.example.pokemontypesapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pokemontypesapp"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true // Activa ViewBinding si lo necesitas
    }
    dataBinding {
        enable = true // Activa DataBinding si lo necesitas
    }
}

dependencies {
    // Dependencias de Firebase
    implementation ("com.google.firebase:firebase-messaging:23.1.1")
    implementation ("com.google.firebase:firebase-analytics:21.0.0")

    // Dependencia para Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Dependencia para Glide (para cargar imágenes)
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Dependencia para RecyclerView (para la lista de tipos)
    implementation(libs.androidx.recyclerview)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Dependencia para ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Dependencia para las herramientas de arquitectura de Android
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.activity:activity-ktx:1.7.0")

    // Si usas ViewModel y LiveData (opcional para tu proyecto)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    // Si usas Fragment (para los fragmentos en la aplicación)
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    // Para usar los componentes de Material Design (si es necesario)
    implementation("com.google.android.material:material:1.8.0")

    // Librería de Gson (si usas Gson para la conversión de objetos)
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(libs.filament.android)
    implementation(libs.androidx.junit.ktx)

    // Si usas JUnit para pruebas (opcional, para pruebas unitarias)
    testImplementation("junit:junit:4.13.2")

    // Si usas Espresso para pruebas UI (opcional)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    // ROOM dependencies
    implementation ("androidx.room:room-runtime:2.5.0")
    kapt ("androidx.room:room-compiler:2.5.0")
    implementation ("androidx.room:room-ktx:2.5.0")
}
