package com.nykytenko.tweetstoelastic

import com.nykytenko.config.EsConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}

class DataProcessor() {

}

object DataProcessor {
  def apply()(stream: InputDStream[ConsumerRecord[String, String]]): DStream[Map[String, String]] = {

    stream.map(s => Map("tweetId" -> s.key() + "tweetText" -> s.value()))


  }
}
