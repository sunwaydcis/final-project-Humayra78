package Project.figure

import Project.ChessField

import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer

class King(color: Color, field: ChessField) extends Figure(color, "king", field) {

  override def getAccessibleFields(): java.util.List[ChessField] = {
    val fields = ListBuffer[ChessField]()
    val attackedFields = field.getBoard.getAllAccessibleFields(color.revert()).asScala.toSet

    // Check surrounding fields for accessibility
    for {
      kx <- -1 to 1
      ky <- -1 to 1
      if !(kx == 0 && ky == 0) // Skip current field
      f = field.getBoard.getField(field.getX + kx, field.getY + ky)
      if f != null && !attackedFields.contains(f) && (f.getFigure == null || f.getFigure.color != color)
    } {
      fields += f
    }

    // Check for castling
    if (getFirstTurn < 0) {
      // Right castling
      val rookFieldRight = field.getBoard.getField(7, y)
      if (rookFieldRight.getFigure.isInstanceOf[Rook] && rookFieldRight.getFigure.getFirstTurn < 0) {
        val rightClear = (x + 1 until 7).forall { i =>
          val current = field.getBoard.getField(i, y)
          current.getFigure == null && !attackedFields.contains(current)
        }
        if (rightClear) fields += field.getBoard.getField(6, y)
      }

      // Left castling
      val rookFieldLeft = field.getBoard.getField(0, y)
      if (rookFieldLeft.getFigure.isInstanceOf[Rook] && rookFieldLeft.getFigure.getFirstTurn < 0) {
        val leftClear = (x - 1 until 1 by -1).forall { i =>
          val current = field.getBoard.getField(i, y)
          current.getFigure == null && (i <= 1 || !attackedFields.contains(current))
        }
        if (leftClear) fields += field.getBoard.getField(2, y)
      }
    }

    fields.toList.asJava
  }

  def isCheck: Boolean =
    field.getBoard.getAllAccessibleFields(color.revert()).contains(field)

  def isCheckMate: Boolean = {
    if (isCheck) {
      field.getBoard.getFigures(color).asScala.forall(_.getAllAccessibleFields().isEmpty)
    } else {
      false
    }
  }

  def isStaleMate: Boolean = {
    if (!isCheck) {
      field.getBoard.getFigures(color).asScala.forall(_.getAllAccessibleFields().isEmpty)
    } else {
      false
    }
  }

  override def postTurnAction(oldField: ChessField, newField: ChessField, graphic: Boolean): Figure = {
    if (graphic && getFirstTurn < 0) {
      if (x == 2) {
        field.getBoard.getField(0, y).getFigure.move(field.getBoard.getField(3, y), graphic)
      } else if (x == 6) {
        field.getBoard.getField(7, y).getFigure.move(field.getBoard.getField(5, y), graphic)
      }
    }
    null
  }
}
