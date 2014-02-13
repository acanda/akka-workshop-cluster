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
import scala.annotation.tailrec

object PopulationActor {

  object Broadcast

}

/**
 * The PopulationActor keeps track of the population and 
 * picks random robots which it sends to the MutationActors
 * or to the CrossoverActors.
 */
class PopulationActor(broadcast: ActorRef, mutationRefs: Seq[ActorRef], crossoverRefs: Seq[ActorRef]) extends Actor {
  import clashcode.PopulationActor.Broadcast
  import clashcode.MutationActor.Mutate
  import clashcode.MutationActor.Mutated
  import clashcode.CrossoverActor.Cross
  import clashcode.CrossoverActor.Crossed

  val populationSize = 100
  var population = List.fill(populationSize)(RobotCode.createRandomCode("Philip").evaluate).sortWith(_.points > _.points)
  var mutations = 0;
  var crossovers = 0;

  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)) {
    self ! Broadcast // send message to myself every 10 seconds
  }
  
  // start mutation and crossover
  mutationRefs.foreach(_ ! Mutate(randomRobot))
  crossoverRefs.foreach(_ ! Cross(randomRobot, randomRobot))

  def receive = {

    case Broadcast =>
      broadcast ! population.head
      broadcast ! randomRobot
      println(s"Best robot: ${population.head.points}, mutations: $mutations, crossovers: $crossovers")

    case Mutated(robots) =>
      addToPopulation(robots)
      mutations += robots.length
      sender ! Mutate(randomRobot)

    case Crossed(robots) =>
      addToPopulation(robots)
      crossovers += robots.length
      sender ! Cross(randomRobot, randomRobot)

    case robot: Robot =>
      addToPopulation(List(robot))

  }

  def addToPopulation(robots: List[Robot]) =
    population = (robots ++ population).sortWith(_.points > _.points).take(populationSize)

  /**
   * Selects a random robot from the population. 
   * The probability of a robot being selected depends
   * on its points (more points -> higher probability).
   */
  def randomRobot: Robot = {

    // The weighted selection works only if all weights are positive.
    // The correction value is used to calculate positive weights for
    // robots with negative points.
    val correction = math.max(0, -population.map(_.points).min)
    
    def weight(robot: Robot): Int = robot.points + correction

    @tailrec
    def pick(robots: List[Robot], cut: Int): Robot = {
      if (robots.tail.isEmpty || cut <= 0) robots.head
      else pick(robots.tail, cut - weight(robots.head))
    }

    val totalWeight = population.map(weight(_)).sum 
    pick(population, Random.nextInt(totalWeight))
    
  }

}


