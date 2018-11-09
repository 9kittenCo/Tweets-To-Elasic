package com.nykytenko.tweetstoelastic


import cats.effect.Effect
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies, LocationStrategy}


case class KafkaService() {

  private val preferredHosts: LocationStrategy = LocationStrategies.PreferConsistent
  private val bootstrapServers: String         = "127.0.0.1:9092"
  private val groupId: String                  = "default_group"

  private val properties = {
    Map[String, String](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG        -> bootstrapServers,
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG   -> classOf[StringDeserializer].getName,
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer].getName,
    ConsumerConfig.GROUP_ID_CONFIG                 -> groupId,
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG        -> "earliest"
    )
  }

  def createStreamFromKafka[F[_]](topics: List[String])(ssc: StreamingContext)(implicit E: Effect[F]): InputDStream[ConsumerRecord[String, String]] = {
      KafkaUtils.createDirectStream[String, String](
        ssc,
        preferredHosts,
        ConsumerStrategies.Subscribe[String, String](topics.distinct, properties)
      )
  }
}
