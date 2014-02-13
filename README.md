# Akka Workshop Reloaded

## Example Solutions

There are three different solutions. You can turn them on/off in Main.scala.

### CopyTheBestActor

The CopyTheBestActor is very lazy and lets the other actors do the hard work.
It simply waits for the other actors to broadcast their best robots
which it re-broadcasts with your own name ensuring your name appearing
at the top position in the high score table ;)

### EvolvingActor

The EvolvingActor contains a population of robots which
it evolves by sending itself messages to mutate (MutateRandomRobot)
or crossover (CrossoverRandomRobots).
This is the actor that achieved the almost perfect score of 97980 during the workshop.
It evolves the population slow but steady.

### PopulationActor, MutationActor, CrossoverActor

A system of actors where the PopulationActor keeps track of the population and 
picks random robots which it sends to the MutationActors or to the CrossoverActors.
This system uses every bit of CPU power your computer has to offer. It evolves the
population very fast but also seems to get stuck at a local optimum very fast.


## Quick-start project

* Eclipse and IntelliJ IDEA SBT support plugins already configured
* Requires SBT 0.13.0 (if you have an older version, remove the Eclipse SBT plugin from project/plugins.sbt)

## How-to: use this project

1. Install SBT! See [http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)
1. Clone this repository to your local computer
1. `cd akka-workshop-cluster`
1. Type `sbt`
1. On the SBT prompt type either `eclipse` or `gen-idea` to generate Eclipse and IntelliJ IDEA project files.
1. Import the project into your IDE.
1. Start coding! ;-)

