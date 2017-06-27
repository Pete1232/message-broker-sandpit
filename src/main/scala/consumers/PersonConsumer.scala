package consumers

import akka.pattern.ask
import akka.util.Timeout
import com.spingo.op_rabbit.Message.ConfirmResponse
import com.spingo.op_rabbit.PlayJsonSupport._
import com.spingo.op_rabbit._
import control.RabbitController
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class PersonConsumer extends RabbitController {
  // setup play-json serializer
  implicit val personFormat: OFormat[Person] = Json.format[Person]
  implicit val recoveryStrategy: RecoveryStrategy = RecoveryStrategy.none

  val subscriptionRef: SubscriptionRef = Subscription.run(rabbitControl) {
    import Directives._
    // A qos of 3 will cause up to 3 concurrent messages to be processed at any given time.
    channel(qos = 3) {
      consume(topic(queue("such-message-queue"), List("some-topic.#"))) {
        (body(as[Person]) & routingKey) { (person, key) =>
          /* do work; this body is executed in a separate thread, as
             provided by the implicit execution context */
          println(
            s"""A person named '${person.name}' with age
          ${person.age} was received over '$key'.""")
          ack
        }
      }
    }
  }

  def sendTestMessage(): Future[ConfirmResponse] = {
    subscriptionRef.initialized.map { _ =>

      implicit val timeout = Timeout(5 seconds)
      val received = (
        rabbitControl ? Message.queue(
          Person(name = "Ivanah Tinkle", age = 25),
          queue = "such-message-queue")
        ).mapTo[ConfirmResponse]
      received
    }.flatMap { x =>
      subscriptionRef.closed
      x
    }
  }
}

case class Person(name: String, age: Int)
