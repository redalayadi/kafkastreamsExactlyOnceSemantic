name := "BankbalanceExactlyOnceSemantic"

version := "0.1"

scalaVersion := "2.12.8"


// needed to resolve weird dependency
libraryDependencies += "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts(
  Artifact("javax.ws.rs-api", "jar", "jar"))

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.0.1",
  "org.slf4j" %  "slf4j-api" % "1.7.25",
  "org.slf4j" %  "slf4j-log4j12" % "1.7.25"
)
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.0.1"


//scalaTest
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

// leverage java 8
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions := Seq("-target:jvm-1.8")
initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}