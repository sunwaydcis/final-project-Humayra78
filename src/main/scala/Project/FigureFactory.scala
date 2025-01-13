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
    val boardField = ChessGame.getBoard.getField(x, y)
    val figure = `type`.toLowerCase match {
      case "rook"   => new Rook(color, boardField)
      case "knight" => new Knight(color, boardField)
      case "bishop" => new Bishop(color, boardField)
      case "queen"  => new Queen(color, boardField)
      case "king"   => new King(color, boardField)
      case "pawn"   => new Pawn(color, boardField)
//      case _        => throw new IllegalArgumentException(s"Unknown Figure: '$`type`'")
    }
    figure.setFirstTurn(firstTurn)
    figure
  }

  def createFigure(color: Color, `type`: Int, x: Int, y: Int, firstTurn: Int): Figure = {
    if (x < 0 || x > 7 || y < 0 || y > 7) {
      throw new IllegalArgumentException(s"Position not on board: '$x/$y'")
    }
    val boardField = ChessGame.getBoard.getField(x, y)
    val figure = `type` match {
      case 0x0      => new Pawn(color, boardField)
      case 0x4    => new Rook(color, boardField)
      case 0x5    => new Knight(color, boardField)
      case 0x6    => new Bishop(color, boardField)
      case 0xe   => new Queen(color, boardField)
      case 0xf   => new King(color, boardField)
//      case _        => throw new IllegalArgumentException(s"Unknown Figure: '$`type`'")
    }
    figure.setFirstTurn(firstTurn)
    figure
  }

  def getFigureID(f: Figure): Int = {
    f match {
      case _: Pawn   => 0x0
      case _: Rook   => 0x4
      case _: Knight => 0x5
      case _: Bishop => 0x6
      case _: Queen  => 0xe
      case _: King   => 0xf
      case _         => 0
    }
  }
}
