pluginManagement {
    repositories {
        // mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "intellij-shellcheck-plugin"