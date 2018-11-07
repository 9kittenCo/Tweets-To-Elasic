package com.nykytenko

import pureconfig.error.ConfigReaderFailures

package object config {


  case class SparkConfig(name: String, master: String)

  case class KafkaConfig(topic: String)

  case class EsConfig(indexName: String, cType: String)

  case class AppConfig(spark: SparkConfig, kafka: KafkaConfig, es: EsConfig)

  import pureconfig._

  def load: Either[ConfigReaderFailures, AppConfig] = loadConfig[AppConfig]

}
