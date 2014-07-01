package scaldi.akka

import scaldi.Module
import akka.actor.ActorSystem
import akka.actor.Inbox
import scala.concurrent.duration._

object SumApplication extends App with AkkaInjectable {

  implicit val applicationModule = new SumModule :: new AkkaModule
  implicit val system = inject[ActorSystem]
  val sumCalculater = injectActorRef[SumCalculater]
  val inbox = Inbox.create(system)

  inbox send (sumCalculater, Sum(3, 4))
  val sum = inbox.receive(5 seconds)
  println(sum)
}

class SumModule extends Module {
  binding toProvider new SumCalculater
  binding toProvider new SumActor
}

class AkkaModule extends Module {
  bind[ActorSystem] to ActorSystem("SumActorSystem") destroyWith (_.shutdown())
}