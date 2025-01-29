plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kgit"
include("ak")
include("cli")
include("repl")
