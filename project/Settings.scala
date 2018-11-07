import sbt.Keys._

object Settings {
  val common = Seq(
    name         := "Tweets-To-Elastic",
    organization := "com.nykytenko",
    version      := "0.0.1-SNAPSHOT",
    scalaVersion := "2.11.12"
  )
}
