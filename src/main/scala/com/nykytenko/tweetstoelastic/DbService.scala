package com.nykytenko.tweetstoelastic

import com.nykytenko.config.EsConfig
import org.apache.spark.streaming.dstream.DStream
import org.elasticsearch.spark._

class DbService {
}

object DbService {
  def persist(esConf: EsConfig)(data: DStream[Map[String, String]]): Unit = data.foreachRDD{
    tweetRDD => tweetRDD.saveToEs(esConf.indexName + "/" + esConf.cType)
  }

}
