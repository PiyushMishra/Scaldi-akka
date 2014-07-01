package scaldi.akka

import akka.actor.Actor
import scaldi.Injector
import akka.actor.ActorRef
import akka.actor.PoisonPill

class SumCalculater(implicit inj: Injector) extends Actor with AkkaInjectable {
  
  val sumActorProps = injectActorProps[SumActor]

  def receive = work

  val work: Receive = {
    case sum: Sum =>

      val sumActor = context.actorOf(sumActorProps)
      sumActor ! sum
      context become doSumAndSendResult(sum, sender)

      def doSumAndSendResult(sum: Sum, reportTo: ActorRef): Receive = {
        case SumCalculated(sum) =>
          println("calculated sum.....")
          reportTo ! sum
      }
  }
}

class SumActor extends Actor {
  def receive = {
    case Sum(a, b) =>
      sender ! SumCalculated(a + b)
  }
}

case class Sum(a: Int, b: Int)
case class SumCalculated(sum: Int)
