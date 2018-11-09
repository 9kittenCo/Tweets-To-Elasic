package com.nykytenko.tweetstoelastic

import cats.effect.Effect
import com.nykytenko.config.SparkConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

case class SparkConfigService[F[_]]()(implicit E: Effect[F]) {
  def load(conf: SparkConfig): StreamingContext = {
    val sparkConf = new SparkConf()
      .setAppName(conf.name)
      .setMaster(conf.master)

    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.mapping.id", "id")

    new StreamingContext(sparkConf, Seconds(2))

  }

  def close(ssc: StreamingContext): Unit = ssc.stop()

}
