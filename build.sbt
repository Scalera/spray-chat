name := "spray-chat"

version := "0.1.0"

scalaVersion  := "2.11.2"

scalacOptions ++= Seq(
  "-deprecation", 
  "-unchecked", 
  "-Xlint",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

val sprayV = "1.3.2"
val akkaV = "2.3.6"
    
libraryDependencies ++= Seq(
  "io.spray"          %% "spray-can"      % sprayV,
  "io.spray"          %% "spray-routing"  % sprayV,
  "io.spray"          %% "spray-httpx"    % sprayV,
  "io.spray"          %% "spray-json"     % "1.3.1",
  "io.spray"          %% "spray-testkit"  % sprayV  % "test",
  "com.typesafe.akka" %% "akka-actor"     % akkaV,
  "com.typesafe.akka" %% "akka-testkit"   % akkaV   % "test",
  "org.scalatest"     %% "scalatest"      % "2.2.2" % "test"
)

Revolver.settings
