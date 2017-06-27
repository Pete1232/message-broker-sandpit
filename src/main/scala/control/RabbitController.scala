package control

import akka.actor.{ActorRef, ActorSystem, Props}
import com.spingo.op_rabbit.RabbitControl

trait RabbitController {
  implicit val actorSystem = ActorSystem("such-system")
  val rabbitControl: ActorRef = actorSystem.actorOf(Props[RabbitControl])
}
