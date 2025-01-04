package tictactoe.actors

import akka.actor.{Actor, ActorRef, Props}
import java.util.UUID
import tictactoe.model.Player
import tictactoe.messages._
import scala.io.StdIn.readLine

class PlayerActor(player: Player, matchmaker: ActorRef) extends Actor {
  def receive = {
    case StartMatchmaking =>
      matchmaker ! JoinQueue(player.id, self)
    case GameStarted =>
      println(s"Player ${player.name} (${player.id}): Game has started!")
    case RequestMove =>
      println(s"Player ${player.name} (${player.id}): It's your turn!")
      // Prompt user for manual input
      val row = readLine("Enter row (0, 1, or 2): ").toInt
      val col = readLine("Enter column (0, 1, or 2): ").toInt
      sender() ! MakeMove(player.id, row, col)
    case GameResult(winner) =>
      winner match {
        case Some(symbol) => println(s"Player ${player.name}: Player $symbol wins!")
        case None => println(s"Player ${player.name}: It's a draw!")
      }
    case _ => println("Unknown message")
  }
}

object PlayerActor {
  def props(player: Player, matchmaker: ActorRef): Props = Props(new PlayerActor(player, matchmaker))
}
