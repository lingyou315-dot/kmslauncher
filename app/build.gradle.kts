// 文件功能：应用级构建脚本
// 修正：增加了 plugins 声明和 kotlinOptions 配置，解决 JVM 21 目标不识别问题
plugins {
    id("com.android.application") // 声明这是一个安卓应用程序插件
    id("org.jetbrains.kotlin.android") // 声明使用 Kotlin 进行开发插件
}
android {
    namespace = "com.company.kmslauncher" // 定义应用的包名空间
    compileSdk = 33 // 设置编译使用的安卓 SDK 版本
    buildFeatures {
        aidl = true // 开启 AIDL 硬件接口支持
    }
    defaultConfig {
        applicationId = "com.company.kmslauncher" // 应用程序的唯一标识 ID
        minSdk = 26 // 最低支持 Android 8.0 系统
        targetSdk = 29 // 目标适配 Android 10 系统（你的真机版本）
        versionCode = 1 // 内部版本号
        versionName = "1.0" // 外部版本名称
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // 设置 Java 源码兼容版本为 1.8
        targetCompatibility = JavaVersion.VERSION_1_8 // 设置 Java 字节码目标版本为 1.8
    }
    // 关键修正：显式指定 Kotlin 的编译目标为 1.8，防止系统误认为 21 导致报错
    kotlinOptions {
        jvmTarget = "1.8" // 将 Kotlin 编译目标强制设为兼容性最好的 1.8
    }
}
dependencies {
    // 引入最强播放器 Media3 ExoPlayer 核心库
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    // 引入 Media3 专用的 UI 播放控件库
    implementation("androidx.media3:media3-ui:1.1.0")
    // 引入基础媒体能力库
    implementation("androidx.media3:media3-common:1.1.0")

    // 基础支持库：界面、材料设计、约束布局
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}