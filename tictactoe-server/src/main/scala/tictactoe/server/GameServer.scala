package tictactoe.server

import akka.actor.{ActorSystem, Props}
import tictactoe.actors.Matchmaker

object GameServer extends App {
  val system = ActorSystem("TicTacToeServer")
  val matchmaker = system.actorOf(Matchmaker.props, "matchmaker")

  println(s"Game server is running... Matchmaker actor path: ${matchmaker.path}")
}
