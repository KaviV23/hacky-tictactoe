package tictactoe.actors

import akka.actor.{Actor, ActorRef, Props}
import tictactoe.model.{Player, Board}
import tictactoe.messages._
import java.util.UUID

class GameManager extends Actor {
  var players = Map.empty[UUID, ActorRef]
  var gameBoard = new Board()
  var currentPlayer: Option[UUID] = None

  def receive = {
    case JoinGame(playerId, playerRef) =>
      players += (playerId -> playerRef)
      println(s"Player joined: $playerId, Total Players: ${players.size}")
      if (players.size == 2) {
        currentPlayer = Some(players.keys.head) // Set the first player as the current player
        players.values.foreach { playerRef =>
          println(s"Sending GameStarted to player: ${playerRef.path}")
          playerRef ! GameStarted
        }
        requestNextMove()
      }
    case MakeMove(playerId, row, col) =>
      if (currentPlayer.contains(playerId)) {
        val symbol = if (players.keys.head == playerId) 'X' else 'O'
        val moveValid = gameBoard.makeMove(symbol, row, col)
        if (moveValid) {
          val boardState = gameBoard.getBoardState
          players.values.foreach(_ ! BoardState(boardState))
          val winner = gameBoard.checkWinner()
          println(s"Move made by: $playerId at ($row, $col), Winner: $winner")
          if (winner.isDefined || gameBoard.isFull()) {
            players.values.foreach(_ ! GameResult(winner))
          } else {
            // Switch current player and request next move
            currentPlayer = players.keys.find(_ != playerId)
            requestNextMove()
          }
        } else {
          println(s"Invalid move by: $playerId, position ($row, $col) is already taken.")
          players(playerId) ! InvalidMove
          requestNextMove() // Request the same player to make another move
        }
      } else {
        println(s"Invalid move by: $playerId, it's not their turn.")
      }
    case _ => println("Unknown message")
  }

  def requestNextMove(): Unit = {
    currentPlayer.foreach { playerId =>
      print(s"requesting move from ${players(playerId)}")
      players(playerId) ! RequestMove
    }
  }
}

object GameManager {
  def props: Props = Props[GameManager]
}
