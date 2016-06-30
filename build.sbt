name := """test-app"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  javaCore,
  javaJpa,
  evolutions,
  "javax.money" % "money-api" % "1.0",
  "com.typesafe.play" %% "play-mailer" % "5.0.0-M1",
  "org.mockito" % "mockito-core" % "2.0.52-beta",
  "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5",
  "com.sendgrid" % "sendgrid-java" % "3.0.0",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// -------------  Custom ------------------ //
// Java project. Don't expect Scala IDE
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

// Use .class files instead of generated .scala files for views and routes
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

routesGenerator := InjectedRoutesGenerator
