package tictactoe.messages

import java.util.UUID
import akka.actor.ActorRef

// Message definitions
case class JoinQueue(playerId: UUID, playerRef: ActorRef)
case class JoinGame(playerId: UUID, playerRef: ActorRef)
case class MakeMove(playerId: UUID, row: Int, col: Int)
case object StartMatchmaking
case object RequestMove
case object GameStarted
case class GameResult(winner: Option[Char])
