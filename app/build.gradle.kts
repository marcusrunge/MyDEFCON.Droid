import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.marcusrunge.mydefcon"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.marcusrunge.MyDEFCON"
        minSdk = 30
        versionCode = 302
        versionName = "2.1.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    sourceSets.configureEach {
        java.srcDir("src/$name/kotlin")
    }

    applicationVariants.configureEach {
        resValue("string", "versionName", versionName)
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.gms:play-services-oss-licenses:17.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":notifications"))
    implementation(project(":firebase"))
    implementation("androidx.work:work-runtime-ktx:2.11.0")
    implementation("androidx.hilt:hilt-work:1.3.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.firebase:firebase-installations:19.0.1")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    ksp("androidx.hilt:hilt-compiler:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
