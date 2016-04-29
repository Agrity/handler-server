name := """test-app"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  javaCore,
  javaJpa,
  evolutions,
  "javax.money" % "money-api" % "1.0",
  "com.typesafe.play" %% "play-mailer" % "4.0.0",
  "org.mockito" % "mockito-core" % "2.0.52-beta"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// -------------  Custom ------------------ //
// Java project. Don't expect Scala IDE
//EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Use .class files instead of generated .scala files for views and routes
//EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

routesGenerator := InjectedRoutesGenerator
