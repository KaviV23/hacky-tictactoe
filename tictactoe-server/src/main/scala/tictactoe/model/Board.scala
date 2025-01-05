package tictactoe.model

class Board {
  private val board: Array[Array[Char]] = Array.fill(3, 3)('_')

  def makeMove(symbol: Char, row: Int, col: Int): Unit = {
    if (board(row)(col) == '_') {
      board(row)(col) = symbol
    } else {
      println("Invalid move, spot already taken.")
    }
  }

  def getBoardState: Array[Array[Char]] = board.map(_.clone())

  def checkWinner(): Option[Char] = {
    // Check rows, columns, and diagonals for a winner
    for (i <- 0 until 3) {
      if (board(i).forall(_ == board(i)(0)) && board(i)(0) != '_') return Some(board(i)(0))
      if ((0 until 3).forall(j => board(j)(i) == board(0)(i)) && board(0)(i) != '_') return Some(board(0)(i))
    }
    if ((0 until 3).forall(i => board(i)(i) == board(0)(0)) && board(0)(0) != '_') return Some(board(0)(0))
    if ((0 until 3).forall(i => board(i)(2 - i) == board(0)(2)) && board(0)(2) != '_') return Some(board(0)(2))
    None
  }

  def isFull(): Boolean = board.flatten.forall(_ != '_')
}
