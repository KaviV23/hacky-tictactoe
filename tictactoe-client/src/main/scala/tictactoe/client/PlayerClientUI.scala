package tictactoe.client

import akka.actor.{ActorRef, ActorSelection, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import tictactoe.actors.PlayerActor
import tictactoe.messages.{MakeMove, StartMatchmaking}
import tictactoe.model.Player
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object PlayerClientUI extends JFXApp {

  // Akka ActorSystem and Timeout
  implicit val system: ActorSystem = ActorSystem("TicTacToeClient")
  implicit val timeout: Timeout = Timeout(5.seconds)

  // Player and Actor variables
  private var matchmaker: ActorRef = _
  private var playerActor: ActorRef = _
  private val player = Player(UUID.randomUUID(), "Player")

  // UI Elements
  private val grid = new GridPane {
    hgap = 5
    vgap = 5
    padding = Insets(10)
  }

  private val statusLabel = new Label("Waiting to start...") {
    font = Font("Arial", 16)
    textFill = Color.DarkBlue
  }

  private val cells: Array[Array[Button]] = Array.fill(3, 3) {
    new Button {
      minWidth = 50
      minHeight = 50
      font = Font("Arial", 18)
    }
  }

  // Setup UI grid and cell event handlers
  for (row <- 0 until 3; col <- 0 until 3) {
    val cell = cells(row)(col)
    grid.add(cell, col, row)
    cell.onAction = _ => handleMove(row, col)
  }

  // ScalaFX Primary Stage
  stage = new PrimaryStage {
    title = "Tic Tac Toe"
    scene = new Scene {
      content = new VBox {
        spacing = 10
        padding = Insets(10)
        children = Seq(statusLabel, grid)
      }
    }
  }

  // Initialize the Matchmaker ActorRef
  val matchmakerSelection: ActorSelection = system.actorSelection("akka://TicTacToeServer@localhost:25555/user/matchmaker")

  (matchmakerSelection ? akka.actor.Identify(0)).map {
    case akka.actor.ActorIdentity(_, Some(matchmakerRef)) =>
      println(s"Resolved Matchmaker actor: $matchmakerRef")
      matchmaker = matchmakerRef
      initializePlayerActor()
    case akka.actor.ActorIdentity(_, None) =>
      println("Matchmaker actor not found.")
    case ex =>
      println(s"Failed to resolve Matchmaker actor: $ex")
  }

  // Initialize the Player Actor
  private def initializePlayerActor(): Unit = {
    playerActor = system.actorOf(PlayerActor.props(player, matchmaker), player.id.toString)
    playerActor ! StartMatchmaking
    Platform.runLater {
      statusLabel.text = "Matchmaking started..."
    }
  }

  // Handle Player Moves
  private def handleMove(row: Int, col: Int): Unit = {
    cells(row)(col).disable = true
    playerActor ! MakeMove(player.id, row, col)
    Platform.runLater {
      statusLabel.text = s"Move made: ($row, $col)"
    }
  }

  def setStatusLabelText(text: String): Unit = {
    Platform.runLater {
      statusLabel.text = text
    }
  }
}
