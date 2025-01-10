package Project

import Project.figure._

object FigureFactory {

  def createFigure(color: Color, `type`: String, pos: String, firstTurn: Int): Figure = {
    if (pos.length != 2) {
      throw new IllegalArgumentException(s"Invalid position: '$pos'")
    }
    val x = pos.toLowerCase.charAt(0) - 'a'
    val y = 7 - (pos.charAt(1) - '1')
    if (x < 0 || x > 7 || y < 0 || y > 7) {
      throw new IllegalArgumentException(s"Position not on board: '$pos'")
    }
    val boardField = ChessGame.getBoard().getField(x, y)
    val figure = `type`.toLowerCase match {
      case "rook"   => new Rook(color, boardField)
      case "knight" => new Knight(color, boardField)
      case "bishop" => new Bishop(color, boardField)
      case "queen"  => new Queen(color, boardField)
      case "king"   => new King(color, boardField)
      case "pawn"   => new Pawn(color, boardField)
      case _        => throw new IllegalArgumentException(s"Unknown Figure: '$`type`'")
    }
    figure.setFirstTurn(firstTurn)
    figure
  }

  def createFigure(color: Color, `type`: Int, x: Int, y: Int, firstTurn: Int): Figure = {
    if (x < 0 || x > 7 || y < 0 || y > 7) {
      throw new IllegalArgumentException(s"Position not on board: '$x/$y'")
    }
    val boardField = ChessGame.getBoard().getField(x, y)
    val figure = `type` match {
      case 0b0      => new Pawn(color, boardField)
      case 0b100    => new Rook(color, boardField)
      case 0b101    => new Knight(color, boardField)
      case 0b110    => new Bishop(color, boardField)
      case 0b1110   => new Queen(color, boardField)
      case 0b1111   => new King(color, boardField)
      case _        => throw new IllegalArgumentException(s"Unknown Figure: '$`type`'")
    }
    figure.setFirstTurn(firstTurn)
    figure
  }

  def getFigureID(f: Figure): Int = {
    f match {
      case _: Pawn   => 0b0
      case _: Rook   => 0b100
      case _: Knight => 0b101
      case _: Bishop => 0b110
      case _: Queen  => 0b1110
      case _: King   => 0b1111
      case _         => 0
    }
  }
}
