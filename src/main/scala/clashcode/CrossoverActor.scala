package clashcode

import akka.actor._
import clashcode.robot._
import akka.cluster.routing.ClusterRouterConfig
import akka.routing.BroadcastRouter
import akka.cluster.routing.ClusterRouterSettings
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Random
import scala.collection.immutable.Range

object CrossoverActor {

  /**
   * Accepted by CrossoverActor. Contains the parent robots.
   */
  case class Cross(robot1: Robot, robot2: Robot)

  /**
   * Sent by CrossoverActor. Contains the child robots.
   */
  case class Crossed(robots: List[Robot])

}

/**
 * The CrossoverActor performs a one-point crossover on
 * two parent robots resulting in two child robots.
 */
class CrossoverActor(robotName: String) extends Actor {
  import clashcode.CrossoverActor.Cross
  import clashcode.CrossoverActor.Crossed

  def receive = {

    case Cross(robot1, robot2) =>
      val code1 = robot1.code
      val code2 = robot2.code
      val cut = Random.nextInt(code1.code.length)
      val child1 = RobotCode(code1.code.take(cut) ++ code2.code.drop(cut), robotName, List(code1, code2)).evaluate
      val child2 = RobotCode(code2.code.take(cut) ++ code1.code.drop(cut), robotName, List(code1, code2)).evaluate
      sender ! Crossed(List(child1, child2))

  }

}


