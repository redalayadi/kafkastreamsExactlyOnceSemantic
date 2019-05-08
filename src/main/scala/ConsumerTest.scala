//import java.util.{Collections, Properties}
//import scala.collection.JavaConverters._
//
//import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
//
//object ConsumerTest {
//
//  def main(args: Array[String]): Unit = {
//
//    val config: Properties = new Properties
//
//    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
//    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
//    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
//    config.put(ConsumerConfig.GROUP_ID_CONFIG, "reda1")
//    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//
//
//
//    /**
//      * A consumer is instantiated by providing the configuration.
//      */
//    val consumer: KafkaConsumer[Nothing, String] = new KafkaConsumer[Nothing, String](config)
//
//    /**
//      * Subscribe to the given list of topics to get dynamically assigned partitions.
//      */
//    consumer.subscribe(Collections.singletonList(topic))
//
//    /**
//      * Infinite loop to read from topic as soon as it gets the record
//      */
//    while (true) {
//      /**
//        * Fetch data for the topics or partitions specified using one of the subscribe/assign APIs.
//        */
//      val records: ConsumerRecords[Nothing, String] = consumer.poll(0)
//      for (record <- records.asScala) {
//        println(record)
//      }
//    }
//  }
//
//}
