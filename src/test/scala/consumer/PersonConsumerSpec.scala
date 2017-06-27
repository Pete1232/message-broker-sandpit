package consumer

import com.spingo.op_rabbit.Message.Ack
import consumers.PersonConsumer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{MustMatchers, WordSpec}

class PersonConsumerSpec extends WordSpec with MustMatchers with ScalaFutures {

  override implicit val patienceConfig = PatienceConfig(Span(4, Seconds))

  val classUnderTest = new PersonConsumer

  "sendTestMessage" must {
    "send a message to rabbit" in {
      whenReady(classUnderTest.sendTestMessage()) {
        _ mustBe Ack(0)
      }
    }
  }
}
