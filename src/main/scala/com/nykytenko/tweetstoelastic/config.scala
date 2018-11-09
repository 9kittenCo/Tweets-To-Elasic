package com.nykytenko

import cats.effect.Effect
import cats.implicits._
import pureconfig.error.ConfigReaderException

package object config {

  case class SparkConfig(name: String, master: String)

  case class KafkaConfig(topic: String)

  case class EsConfig(indexName: String, cType: String)

  case class AppConfig(spark: SparkConfig, kafka: KafkaConfig, es: EsConfig)

  import pureconfig._

  def load[F[_]](implicit E: Effect[F]): F[AppConfig] = E.delay {
      loadConfig[AppConfig]
    }.flatMap {
      case Right(config) => E.pure(config)
      case Left(e)       => E.raiseError(new ConfigReaderException[AppConfig](e))
    }
}
