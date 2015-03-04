name := "simpleBTApp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.braintreepayments.gateway" % "braintree-java" % "2.40.0"
)


play.Project.playJavaSettings
