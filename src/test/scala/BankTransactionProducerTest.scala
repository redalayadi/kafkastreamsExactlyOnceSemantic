
import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest._


class BankTransactionProducerTest extends FunSuite with Matchers {


  test("producer test") {

    val producer = BankTransactionProducer.newRandomTransaction("jhon")
    val key = producer.key()
    val value = producer.value()
    key shouldBe "jhon"

    println(value)

//
//    val mapper = new ObjectMapper()
//    try{
//      val jsonNode = mapper.readTree(value)
//      jsonNode.get("name") shouldBe "jhon"
//    } catch {
//      case e : Exception => e.printStackTrace()
//    }
  }


  test("newbalance"){

  val jsontransaction =
    """
      |{"name":"jhon",
      |"amout":91,
      |"time":"2019-05-08T13:40:16.750Z"}
    """.stripMargin
    val jsonBalance =
      """
        |{"count":0,
        |"balance":100,
        |"time":"2019-05-08T13:43:16.750Z"}
      """.stripMargin

    val mapper = new ObjectMapper()


    val jsonNodetransaction = mapper.readTree(jsontransaction)
    val jsonNodeBalance = mapper.readTree(jsonBalance)

    val balance = BanlBalanceExactlyOnceApp.newBalance(jsonNodetransaction, jsonNodeBalance)



  }
}
