import sbt._
import sbt.Keys._
import xml.Group
import aether._
import AetherKeys._

object Build extends sbt.Build {

  val liftJSONversion = "2.4"

  lazy val buildSettings = Defaults.defaultSettings ++ Aether.aetherSettings ++ Seq(
    organization := "net.hamnaberg.rest",
    scalaVersion := "2.9.1",
    scalacOptions := Seq("-deprecation"),
    deployRepository <<= (version) apply {
      (v: String) => if (v.trim().endsWith("SNAPSHOT")) Resolvers.sonatypeNexusSnapshots else Resolvers.sonatypeNexusStaging
    },
    pomIncludeRepository := { x => false },
    aetherCredentials := {
      val cred = Path.userHome / ".sbt" / ".credentials"
      if (cred.exists()) Some(Credentials(cred)) else None
    }
  )

  lazy val root = Project(
    id = "json-collection",
    base = file("."),
    settings = buildSettings ++ Seq(
      description := "Collection+JSON",
      name := "scala-json-collection", 
      libraryDependencies := Seq(
        "net.liftweb" %% "lift-json" % liftJSONversion,
        "org.specs2" %% "specs2" % "1.11" % "test"        
      ), 
	    manifestSetting,
        publish <<= Aether.deployTask.init	    
	    ) ++ mavenCentralFrouFrou
	  )

	  object Resolvers {
	    val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	    val sonatypeNexusStaging = "Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
	  }

	  lazy val manifestSetting = packageOptions <+= (name, version, organization) map {
	    (title, version, vendor) =>
	      Package.ManifestAttributes(
	        "Created-By" -> "Simple Build Tool",
	        "Built-By" -> System.getProperty("user.name"),
	        "Build-Jdk" -> System.getProperty("java.version"),
	        "Specification-Title" -> title,
	        "Specification-Version" -> version,
	        "Specification-Vendor" -> vendor,
	        "Implementation-Title" -> title,
	        "Implementation-Version" -> version,
	        "Implementation-Vendor-Id" -> vendor,
	        "Implementation-Vendor" -> vendor
	      )
	  }

	  // Things we care about primarily because Maven Central demands them
	  lazy val mavenCentralFrouFrou = Seq(
	    homepage := Some(new URL("http://github.com/hamnis/scala-collection-json/")),
	    startYear := Some(2011),
	    licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
	    pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
	      <scm>
	        <url>http://github.com/hamnis/scala-collection-json</url>
	        <connection>scm:git:git://github.com/hamnis/scala-collection-json.git</connection>
	        <developerConnection>scm:git:git@github.com:hamnis/scala-collection-json.git</developerConnection>
	      </scm>
	      <developers>
	        <developer>
	          <id>hamnis</id>
	          <name>Erlend Hamnaberg</name>
	          <url>http://twitter.com/hamnis</url>
	        </developer>
	      </developers>
	    )}
	  )
	}
