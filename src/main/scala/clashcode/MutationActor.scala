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

object MutationActor {

  /**
   * Accepted by MutationActor. Contains the robot that will be mutated.
   */
  case class Mutate(robot: Robot)
  
  /**
   * Sent by MutationActor. Contains the mutated robots.
   */ 
  case class Mutated(robots: List[Robot])

}

/**
 * The MutationActor creates several mutated versions of a robot.
 */
class MutationActor(robotName: String) extends Actor {
  import clashcode.MutationActor.Mutate
  import clashcode.MutationActor.Mutated

  def receive = {

    case Mutate(robot) =>
      // mutate 50%, 25%, 10% and 5% of the robot's code
      val mutants = List(50, 25, 10, 5) map { mutate(robot.code, _) }

      val message =
        if (mutants.map(_.points).max <= robot.points) {
          // start fine tuning if none of the mutants is better than the parent robot:
          // the resulting robots each have only one (different) byte changed
          val code = robot.code.code
          Mutated(List.tabulate(code.length)(i => assemble(replace(code, i, randomDecision), robot.code)))

        } else {
          Mutated(mutants)
        }

      sender ! message

  }

  def replace(code: Array[Byte], index: Int, value: Byte): Array[Byte] =
    (code.take(index) :+ value) ++ code.drop(index + 1)

  def mutate(parent: RobotCode, percentage: Int): Robot =
    assemble(mutate(parent.code, percentage), parent)

  def mutate(code: Array[Byte], percentage: Int): Array[Byte] =
    for (c <- code) yield if (Random.nextInt(100) < percentage) randomDecision else c

  def assemble(mutatedCode: Array[Byte], parent: RobotCode): Robot =
    RobotCode(mutatedCode, robotName, List(parent)).evaluate

  def randomDecision = Random.nextInt(Decisions.count).toByte

}


