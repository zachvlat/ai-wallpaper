plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.zachvlat.ai_wallpaper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zachvlat.ai_wallpaper"
        minSdk = 27
        targetSdk = 35
        versionCode = 13
        versionName = "3.6.0"

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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.ui.tooling.preview.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    
    // Material Design 3
    implementation("com.google.android.material:material:1.12.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Image loading
    implementation("com.squareup.picasso:picasso:2.8")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.11.0")
    
    // Dynamic color support
    implementation("androidx.palette:palette-ktx:1.0.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
