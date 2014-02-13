package clashcode

import akka.actor._
import clashcode.robot._
import akka.cluster.routing.ClusterRouterConfig
import akka.routing.BroadcastRouter
import akka.cluster.routing.ClusterRouterSettings

import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._

/**
 * The CopyTheBestActor is very lazy and lets the other actors do the hard work.
 * It simply waits for the other actors to broadcast their best robots
 * which it re-broadcasts with your own name ensuring your name appearing
 * at the top position in the high score table ;)
 */
class CopyTheBestActor(broadcast: ActorRef) extends Actor {

  object SendBestRobot

  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)) {
    self ! SendBestRobot // send message to myself every 10 seconds
  }

  var bestRobot = RobotCode.createRandomCode("Philip").evaluate

  def receive = {

    case SendBestRobot => broadcast ! bestRobot

    case robot: Robot =>
      if (robot.points > bestRobot.points) {
        println("found a better robot " + bestRobot.points + " -> " + robot.points)
        bestRobot = RobotCode(robot.code.code, "Philip", List(robot.code)).evaluate
      }

  }

}


