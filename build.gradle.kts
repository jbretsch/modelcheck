plugins {
    `java-library`
    `maven-publish`
    signing
    idea
    groovy
    id("com.github.ben-manes.versions") version "0.39.0"
    id("nebula.optional-base") version "7.0.0"
}

tasks.jar {
    archiveFileName.set("modelcheck")
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.32")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.8")

    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
    testImplementation("org.mockito:mockito-core:4.1.0")
}

java {
    group = "net.bretti.modelcheck"
    version = "1.0.1-SNAPSHOT"
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "modelcheck"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("modelcheck")
                description.set("modelcheck is a Java library that allows you to check whether a given transition " +
                        "system (described as a Kripke structure) satisfies a given computation tree logic " +
                        "(CTL) formula.")
                url.set("https://github.com/jbretsch/modelcheck")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/jbretsch/modelcheck/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("jbretsch")
                        name.set("Jan Bretschneider")
                        email.set("mail@jan-bretschneider.de")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/jbretsch/modelcheck.git")
                    developerConnection.set("scm:git:ssh://github.com/jbretsch/modelcheck.git")
                    url.set("https://github.com/jbretsch/modelcheck")
                }
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            credentials(PasswordCredentials::class)
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.wrapper {
    gradleVersion = "7.3.2"
    distributionType = Wrapper.DistributionType.ALL
}
