package tictactoe.actors

import akka.actor.{Actor, ActorRef, Props}
import tictactoe.messages._
import java.util.UUID

class Matchmaker extends Actor {
  var waitingPlayers: List[(UUID, ActorRef)] = List.empty

  def receive = {
    case JoinQueue(playerId, playerRef) =>
      waitingPlayers = waitingPlayers :+ (playerId -> playerRef)
      println(s"Player joined queue: $playerId, Waiting Players: ${waitingPlayers.size}")
      if (waitingPlayers.size >= 2) {
        val (player1, player2) = (waitingPlayers.head, waitingPlayers(1))
        waitingPlayers = waitingPlayers.drop(2)
        val gameManager = context.actorOf(GameManager.props, s"gameManager-${UUID.randomUUID()}")
        gameManager ! JoinGame(player1._1, player1._2)
        gameManager ! JoinGame(player2._1, player2._2)
      }
    case _ => println("Unknown message in Matchmaker")
  }
}

object Matchmaker {
  def props: Props = Props[Matchmaker]
}
