package com.nykytenko.tweetstoelastic

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils

object TweetsToKafkaProducer {
  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      System.err.println("Usage: Tweets-To-Elastic <consumer key> <consumer secret> " +
        "<access token> <access token secret> [<filters>]")
      System.exit(1)
    }

    val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    val filters = args.takeRight(args.length - 4)

    val kafkaTopic = "tweets"

    val bootstrapServers = "127.0.0.1:9092"

    System.setProperty("twitter4j.oauth.consumerKey"      , consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret"   , consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken"      , accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    val conf = new SparkConf().setAppName("Tweets-To-Elastic-Producer")
    val ssc = new StreamingContext(conf, Seconds(1))

    val tweets = TwitterUtils.createStream(ssc, None) //todo add auth

    tweets.map{ tweet =>
      val record = new ProducerRecord(kafkaTopic, tweet.getId.toString, tweet.getText)
      kafkaProducer.send(record)
    }
    kafkaProducer.close()

    def kafkaProducer: KafkaProducer[String, String] = {
      val kafkaTopic = "tweets"
      val properties = new Properties()
      properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG      , bootstrapServers)
      properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG   , classOf[StringSerializer].getName)
      properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , classOf[StringSerializer].getName)
      properties.put("request.required.acks", "1")
      new KafkaProducer[String, String](properties)

    }
  }
}
