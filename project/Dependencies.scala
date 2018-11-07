import sbt._

object Dependencies {
  object Version {
    val PureConfig = "0.9.1"
    val Cats       = "1.0.1"
    val CatsEffect = "1.0.0"
    val Spark      = "2.4.0"
    val Kafka      = "2.0.0"
    val Slf4j      = "1.7.25"
  }

  val baseDependencies = Seq(
    "com.github.pureconfig" %% "pureconfig"                 % Version.PureConfig,

    "org.apache.spark"      %% "spark-streaming-twitter"    % "1.6.3",
    "org.twitter4j"         % "twitter4j-stream"            % "4.0.7",

    "org.typelevel"         %% "cats-effect"                % Version.CatsEffect,

    "org.elasticsearch"     %% "elasticsearch-spark"        % "2.4.5",
    
    "org.slf4j"             % "slf4j-simple"                % Version.Slf4j,
    "org.apache.kafka"      % "kafka-clients"               % Version.Kafka,
    
    "org.apache.spark"      %% "spark-streaming-kafka-0-10" % Version.Spark,
    "org.apache.spark"      %% "spark-core"                 % Version.Spark,
    "org.apache.spark"      %% "spark-sql"                  % Version.Spark,
    "org.apache.spark"      %% "spark-streaming"            % Version.Spark
  )
}
