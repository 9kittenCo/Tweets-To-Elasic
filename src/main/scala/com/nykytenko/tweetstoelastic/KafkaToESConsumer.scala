package com.nykytenko.tweetstoelastic

import cats.implicits._
import com.nykytenko.config
import com.nykytenko.config.AppConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.slf4j.{Logger, LoggerFactory}
import pureconfig.error.ConfigReaderFailures

object KafkaToESConsumer {
  case class EtlResult(value: EtlDescription, ssc: StreamingContext)

  val logger: Logger = LoggerFactory.getLogger(KafkaToESConsumer.getClass)

  def main(args: Array[String]): Unit = {
    process match {
      case Left(e) => logger.error(s"Error: $e")
      case Right(etlResult) =>
        etlResult.value.apply()
        etlResult.ssc.start()
        etlResult.ssc.awaitTermination()
    }
  }

  val process: Either[ConfigReaderFailures, EtlResult] = {
    for {
      appConf     <- config.load
      ssc          = SparkConfigService().load(appConf.spark)
    } yield EtlResult(getEtl(appConf), ssc)
  }

  def getEtl(appConf: AppConfig) = new EtlDescription(
                                            ssc = SparkConfigService().load(appConf.spark),
                                            kafkaStream = KafkaService().createStreamFromKafka(List(appConf.kafka.topic)),
                                            process = DataProcessor.apply(),
                                            write = DbService.persist(appConf.es)
                                          )
}

class EtlDescription(
           ssc: StreamingContext,
           kafkaStream: StreamingContext => InputDStream[ConsumerRecord[String, String]],
           process: InputDStream[ConsumerRecord[String, String]] => DStream[Map[String, String]],
           write: DStream[Map[String, String]] => Unit
         ) {
  def apply(): Unit = write(process(kafkaStream(ssc)))

}
