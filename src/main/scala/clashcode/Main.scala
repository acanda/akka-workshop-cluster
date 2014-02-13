package clashcode

import akka.actor.{ Props, ActorSystem }
import akka.routing.BroadcastRouter
import akka.cluster.routing.{ ClusterRouterSettings, ClusterRouterConfig }
import com.typesafe.config.ConfigFactory
import clashcode.robot.RobotCode

object Main extends App {

  override def main(args: Array[String]) {

    // my actor system
    val system = ActorSystem("cluster")

    val broadcastRouter = system.actorOf(Props.empty.withRouter(
      ClusterRouterConfig(
        BroadcastRouter(),
        ClusterRouterSettings(totalInstances = 100, routeesPath = "/user/main", allowLocalRoutees = true, useRole = None))),
      name = "router")

    // val mainActorRef = system.actorOf(Props(classOf[CopyTheBestActor], broadcastRouter), "main")

    val mainActorRef = system.actorOf(Props(classOf[EvolvingActor], broadcastRouter), "main")

    // Set up a system with a PopulationActor and several MutationActors and CrossoverActors
    // val robotName = "Philip"
    // val mutationActorRefs = Seq.tabulate(4)(i => system.actorOf(Props(classOf[MutationActor], robotName), s"mutation-$i"))
    // val crossoverActorRefs = Seq.tabulate(4)(i => system.actorOf(Props(classOf[CrossoverActor], robotName), s"crossover-$i"))
    // val mainActorRef = system.actorOf(Props(classOf[PopulationActor], broadcastRouter, mutationActorRefs, crossoverActorRefs), "main")

    // quit when return key was pressed
    readLine()
    system.shutdown()
  }

}