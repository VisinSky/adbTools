pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google()
        mavenCentral()
        maven{ setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        /*maven {
            url 'https://download.01.org/crosswalk/releases/crosswalk/android/maven2'
        }*/
//        maven { setUrl ("https://code.aliyun.com/louisgeek/maven/raw/master") }
        maven{ setUrl ("https://maven.aliyun.com/nexus/content/repositories/jcenter")}
        gradlePluginPortal()


        maven {
            setUrl ( "https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{ setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        /*maven {
            url 'https://download.01.org/crosswalk/releases/crosswalk/android/maven2'
        }*/
//        maven { setUrl ("https://code.aliyun.com/louisgeek/maven/raw/master") }
        maven{ setUrl ("https://maven.aliyun.com/nexus/content/repositories/jcenter")}
        gradlePluginPortal()


        maven {
            setUrl ( "https://jitpack.io")
        }
    }
}

rootProject.name = "AdbTools"
include(":app")
include(":adb_server")
include(":dadbTools")
include(":kadb")
