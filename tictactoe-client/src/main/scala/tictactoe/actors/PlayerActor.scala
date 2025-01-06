package tictactoe.actors

import akka.actor.{Actor, ActorRef, Props}
import java.util.UUID
import tictactoe.model.Player
import tictactoe.messages._
import tictactoe.client.PlayerClientUI

class PlayerActor(player: Player, matchmaker: ActorRef) extends Actor {
  var gameManager: ActorRef = _

  def receive = {
    case StartMatchmaking =>
      matchmaker ! JoinQueue(player.id, self)
    case GameStarted =>
      gameManager = sender()
      PlayerClientUI.setStatusLabelText(s"Player ${player.name} (${player.id}): Game has started!")
    case RequestMove =>
      PlayerClientUI.setStatusLabelText(s"Player ${player.name} (${player.id}): It's your turn!")
    case BoardState(board) =>
      PlayerClientUI.updateBoard(board)
    case InvalidMove =>
      PlayerClientUI.setStatusLabelText(s"Invalid move, ${player.name}. Please try again.")
    case GameResult(winner) =>
      winner match {
        case Some(symbol) => PlayerClientUI.setStatusLabelText(s"Player ${player.name}: Player $symbol wins!")
        case None => PlayerClientUI.setStatusLabelText(s"Player ${player.name}: It's a draw!")
      }
    case MakeMove(playerId, row, col) =>
      if (gameManager != null) {
        gameManager ! MakeMove(playerId, row, col)
      } else {
        println(s"GameManager is not set for player ${player.name}")
      }
    case msg =>
      println(s"Unknown message: $msg")
  }
}

object PlayerActor {
  def props(player: Player, matchmaker: ActorRef): Props = Props(new PlayerActor(player, matchmaker))
}
