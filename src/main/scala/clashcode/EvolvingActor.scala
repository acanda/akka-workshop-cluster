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

/**
 * The EvolvingActor contains a population of robots which
 * it evolves by sending itself messages to mutate (MutateRandomRobot)
 * or crossover (CrossoverRandomRobots).
 */
class EvolvingActor(broadcast: ActorRef) extends Actor {

  val populationSize = 100

  object SendBestRobot
  object MutateRandomRobot
  object CrossoverRandomRobots

  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)) {
    self ! SendBestRobot // send message to myself every 10 seconds
  }

  // initialize the population
  var population: List[Robot] = List.fill(populationSize)(RobotCode.createRandomCode("Philip").evaluate).sortWith(_.points > _.points)
  
  // initialize the counters
  var mutations = 0
  var crossovers = 0

  // start mutation and crossover
  self ! MutateRandomRobot
  self ! CrossoverRandomRobots

  def receive = {

    case SendBestRobot =>
      broadcast ! population.head
      print("Best robot: " + population.head.points)
      println(s", mutations: $mutations, crossovers: $crossovers")

      
    case robot: Robot => addToPopulation(robot)

    
    case MutateRandomRobot =>
      val robot = randomRobot
      for (c <- 0 to robot.code.code.length - 1) {
        val mutatedCode = (robot.code.code.take(c) :+ Random.nextInt(6).toByte) ++ robot.code.code.drop(c + 1)
        addToPopulation(RobotCode(mutatedCode, "Philip", List(robot.code)).evaluate)
      }
      mutations += robot.code.code.length
      self ! MutateRandomRobot

      
    case CrossoverRandomRobots =>
      val code1 = randomRobot.code
      val code2 = randomRobot.code
      val cut = Random.nextInt(code1.code.length)
      val crossedCode = code1.code.take(cut) ++ code2.code.drop(cut)
      addToPopulation(RobotCode(crossedCode, "Philip", List(code1, code2)).evaluate)
      crossovers += 1
      self ! CrossoverRandomRobots
      
  }

  def addToPopulation(robot: Robot) = {
    population = robot :: population
    population = population.sortWith(_.points > _.points).take(populationSize)
  }

  def randomRobot: Robot = {
    population(Random.nextInt(populationSize))
  }

}


