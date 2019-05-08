import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.connect.json.{JsonDeserializer, JsonSerializer}
import org.apache.kafka.streams.{StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.{Consumed, KTable}
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Serialized
import org.apache.kafka.streams.processor.ThreadMetadata
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.Produced
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import java.time.Instant

import org.apache.kafka.common.utils.Bytes

object BanlBalanceExactlyOnceApp {


  def main(args: Array[String]): Unit = {
    val config = new Properties()

    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE)


    // json Serde// json Serde

    val jsonSerializer = new JsonSerializer
    val jsonDeserializer = new JsonDeserializer
    val jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer)


    val builder: StreamsBuilder = new StreamsBuilder


    val bankTransactions = builder.stream("bank-transactions", Consumed.`with`(Serdes.String, jsonSerde))


    // create the initial json object for balances// create the initial json object for balances

    val initialBalance = JsonNodeFactory.instance.objectNode
    initialBalance.put("count", 0)
    initialBalance.put("balance", 0)
    initialBalance.put("time", Instant.ofEpochMilli(0L).toString)


    val bankBalance: KTable[String, JsonNode] = bankTransactions.
      groupByKey(Serialized.`with`(Serdes.String, jsonSerde)).
      aggregate(() => initialBalance,
        (key, transaction, balance) => newBalance(transaction, balance),
        Materialized.as[String, JsonNode, KeyValueStore[Bytes, Array[Byte]]]("bank-balance-agg")
        .withKeySerde(Serdes.String)
        .withValueSerde(jsonSerde))


    bankBalance.toStream.to("bank-balance-exactly-once", Produced.`with`(Serdes.String, jsonSerde))

    val streams = new KafkaStreams(builder.build, config)
    streams.cleanUp()
    streams.start()

    // print the topology
    streams.localThreadsMetadata.forEach((data: ThreadMetadata) => System.out.println(data))


    // shutdown hook to correctly close the streams application
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        streams.close()
      }
    })


  }


   def newBalance(transaction: JsonNode, balance: JsonNode): JsonNode = {
    // create a new balance json object
    val newBalance = JsonNodeFactory.instance.objectNode
    newBalance.put("count", balance.get("count").asInt + 1)
    newBalance.put("balance", balance.get("balance").asInt + transaction.get("amout").asInt)
    val balanceEpoch = Instant.parse(balance.get("time").asText).toEpochMilli
    val transactionEpoch = Instant.parse(transaction.get("time").asText).toEpochMilli
    val newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch))
    //val newBalanceInstant = Instant.ofEpochMilli(0L).toString

     newBalance.put("time", newBalanceInstant.toString)
    newBalance
  }
}
