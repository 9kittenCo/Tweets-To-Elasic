package com.nykytenko.tweetstoelastic

import cats.effect.{Effect, IO}
import cats.implicits._
import com.nykytenko.config
import com.nykytenko.config.AppConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.slf4j.{Logger, LoggerFactory}

object KafkaToESConsumer {
  case class EtlResult(value: EtlDescription, ssc: StreamingContext)

  val logger: Logger = LoggerFactory.getLogger(KafkaToESConsumer.getClass)

  def main(args: Array[String]): Unit = {
    program[IO]().unsafeRunSync()
 }

  def program[F[_]]()(implicit E: Effect[F]): F[Unit] = {
    for {
      p <- process[F]
      _ <- p.ssc.start
      _ <- p.value.apply[F]
      _ <- p.ssc.stop()
    } yield ()
  }

  def process[F[_]]()(implicit E: Effect[F]): F[EtlResult] = {
    for {
      appConf     <- config.load
      ssc          = SparkConfigService().load(appConf.spark)
    } yield EtlResult(getEtl(appConf), ssc)
  }

  def getEtl[F[_]](appConf: AppConfig)(implicit E: Effect[F]) = new EtlDescription(ssc = SparkConfigService[F]().load(appConf.spark),
      kafkaStream = KafkaService().createStreamFromKafka(List(appConf.kafka.topic)),
      process = DataProcessor.apply(), write = DbService.persist(appConf.es)
    )
}

class EtlDescription(
           ssc: StreamingContext,
           kafkaStream: StreamingContext => InputDStream[ConsumerRecord[String, String]],
           process: InputDStream[ConsumerRecord[String, String]] => DStream[Map[String, String]],
           write: DStream[Map[String, String]] => Unit
         ) {
  def apply[F[_]]()(implicit E: Effect[F]): Unit = E.delay {
    write(process(kafkaStream(ssc)))
  }
}
