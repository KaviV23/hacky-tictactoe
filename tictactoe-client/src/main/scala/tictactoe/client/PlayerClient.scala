package tictactoe.client

import akka.actor.{ActorSelection, ActorSystem, Props}
import tictactoe.actors.PlayerActor
import tictactoe.model.Player
import tictactoe.messages.{JoinGame, JoinQueue, StartMatchmaking}

import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout

object PlayerClient extends App {
  implicit val timeout: Timeout = Timeout(5.seconds)

  val system = ActorSystem("TicTacToeClient")
  val matchmakerSelection: ActorSelection = system.actorSelection("akka://TicTacToeServer@192.168.1.62:25555/user/matchmaker")

  val player = Player(UUID.randomUUID(), "Player")

  // Resolve ActorSelection to ActorRef with detailed logging
  (matchmakerSelection ? akka.actor.Identify(0)).map {
    case akka.actor.ActorIdentity(_, Some(matchmakerRef)) =>
      println(s"Resolved Matchmaker actor: $matchmakerRef")
      val playerActor = system.actorOf(PlayerActor.props(player, matchmakerRef), player.id.toString)

      // Add a delay to ensure actors are initialized
      system.scheduler.scheduleOnce(1.second) {
        println("Starting matchmaking...")
        matchmakerRef ! JoinQueue(player.id, playerActor)
      }
    case akka.actor.ActorIdentity(_, None) =>
      println("GameManager actor not found.")
  }.recover {
    case ex => println(s"Failed to resolve GameManager actor: $ex")
  }
}
