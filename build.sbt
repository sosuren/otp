lazy val commonSettings = Seq(
  organization := "com.otp",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val otpNorthStar = (project in file("."))
  .settings(commonSettings: _*)

unmanagedJars in Compile := (baseDirectory.value **  "*.jar").classpath

val akkaVersion = "2.4.16"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
)