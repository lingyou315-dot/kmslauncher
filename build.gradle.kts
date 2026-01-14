// 文件功能：项目级构建配置，用于定义全局插件版本
// 严禁在此处写具体的业务依赖
plugins {
    // 使用 id 方式定义插件版本，这是目前官方推荐的现代做法
    // 不要使用旧的 buildscript { classpath(...) } 模式
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
}
// 模块化原则：这里可以定义全局变量
// 方便所有子模块共享版本号