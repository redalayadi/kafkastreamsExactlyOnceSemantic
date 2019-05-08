import java.time.Instant
import java.util.Properties
import java.util.concurrent.ThreadLocalRandom

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

object BankTransactionProducer {


  def main(args: Array[String]): Unit = {

    //    val config: Properties = new Properties
    //
    //    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    //    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getClass.get?)
    //
    //    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    //    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    //
    //
    //


    val config: Properties = new Properties

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    config.put(ProducerConfig.ACKS_CONFIG, "all")
    config.put(ProducerConfig.RETRIES_CONFIG, "3")
    config.put(ProducerConfig.LINGER_MS_CONFIG, "1")
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")


    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](config)


    val i = 0
    while (true) {
      println("producing batch" + i)
      try {
        producer.send(newRandomTransaction("john"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("stephane"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("alice"))
        Thread.sleep(100)
        i + 1
      } catch {
        case e: InterruptedException => e.printStackTrace()
      }
    }

    producer.close()

  }

  def newRandomTransaction(name: String): ProducerRecord[String, String] = {


    val transaction = JsonNodeFactory.instance.objectNode()
    val amount = ThreadLocalRandom.current().nextInt(0, 100)
    val now = Instant.now()
    transaction.put("name", name)
    transaction.put("amout", amount)
    transaction.put("time", now.toString)
    new ProducerRecord[String, String]("bank-transactions", name, transaction.toString)
  }


}
