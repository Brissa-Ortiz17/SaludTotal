plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.example.salud_total_v2"
    compileSdkVersion(34)
    defaultConfig {
        applicationId = "com.example.saludtotal_v2"
        minSdkVersion(21)
        targetSdkVersion(34)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

val kotlin_version = "1.8.10"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.5.1")
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // OkHttp dependencies
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}
