package Project.figure

import Project.{ChessField, PromotionDialog}
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer

class Pawn(color: Color, field: ChessField) extends Figure(color, "pawn", field) {
  
  def x: Int = field.getX
  def y: Int = field.getY

  override def getAccessibleFields(): java.util.List[ChessField] = {
    val fields = ListBuffer[ChessField]()
    val board = field.getBoard

    def addMoveIfValid(x: Int, y: Int, condition: Boolean): Unit = {
      val targetField = board.getField(x, y)
      if (targetField != null && condition) fields += targetField
    }

    if (color == Color.WHITE) {
      // Forward moves
      addMoveIfValid(x, y - 1, board.getField(x, y - 1).getFigure == null)
      if (y == 6) addMoveIfValid(x, y - 2, board.getField(x, y - 1).getFigure == null && board.getField(x, y - 2).getFigure == null)

      // Captures
      addMoveIfValid(x + 1, y - 1, x + 1 < 8 && board.getField(x + 1, y - 1).getFigure != null && board.getField(x + 1, y - 1).getFigure.color != color)
      addMoveIfValid(x - 1, y - 1, x - 1 >= 0 && board.getField(x - 1, y - 1).getFigure != null && board.getField(x - 1, y - 1).getFigure.color != color)

      // En passant
      if (y == 3) {
        addMoveIfValid(x - 1, y - 1, x - 1 >= 0 && isValidEnPassant(x - 1, y))
        addMoveIfValid(x + 1, y - 1, x + 1 < 8 && isValidEnPassant(x + 1, y))
      }
    } else {
      // Forward moves
      addMoveIfValid(x, y + 1, board.getField(x, y + 1).getFigure == null)
      if (y == 1) addMoveIfValid(x, y + 2, board.getField(x, y + 1).getFigure == null && board.getField(x, y + 2).getFigure == null)

      // Captures
      addMoveIfValid(x + 1, y + 1, x + 1 < 8 && board.getField(x + 1, y + 1).getFigure != null && board.getField(x + 1, y + 1).getFigure.color != color)
      addMoveIfValid(x - 1, y + 1, x - 1 >= 0 && board.getField(x - 1, y + 1).getFigure != null && board.getField(x - 1, y + 1).getFigure.color != color)

      // En passant
      if (y == 4) {
        addMoveIfValid(x - 1, y + 1, x - 1 >= 0 && isValidEnPassant(x - 1, y))
        addMoveIfValid(x + 1, y + 1, x + 1 < 8 && isValidEnPassant(x + 1, y))
      }
    }

    fields.toList.asJava
  }

  private def isValidEnPassant(x: Int, y: Int): Boolean = {
    val targetField = field.getBoard.getField(x, y)
    targetField != null &&
      targetField.getFigure != null &&
      targetField.getFigure.isInstanceOf[Pawn] &&
      targetField.getFigure.color != color &&
      field.getBoard.getCurrentTurn - targetField.getFigure.getFirstTurn == 1
  }

  override def postTurnAction(oldField: ChessField, newField: ChessField, graphic: Boolean): Figure = {
    val board = field.getBoard
    var capturedPawn: Figure = null

    // En passant
    if (color == Color.BLACK && field.getY == 5) {
      val possiblePawn = board.getField(x, 4).getFigure
      if (possiblePawn.isInstanceOf[Pawn] && board.getCurrentTurn - possiblePawn.getFirstTurn == 1) {
        possiblePawn.getField.setFigure(null, graphic)
        capturedPawn = possiblePawn
      }
    } else if (color == Color.WHITE && field.getY == 2) {
      val possiblePawn = board.getField(x, 3).getFigure
      if (possiblePawn.isInstanceOf[Pawn] && board.getCurrentTurn - possiblePawn.getFirstTurn == 1) {
        possiblePawn.getField.setFigure(null, graphic)
        capturedPawn = possiblePawn
      }
    }

    // Promotion
    if (graphic && (field.getY == 0 || field.getY == 7)) {
      val result = new PromotionDialog(this).showAndWait()
      result.ifPresent(_.move(field, graphic))
    }

    capturedPawn
  }
}
