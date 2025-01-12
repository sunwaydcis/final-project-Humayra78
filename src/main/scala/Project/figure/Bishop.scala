package Project.figure

import Project.ChessField

import scala.collection.mutable.ListBuffer
import scala.util.boundary.break

class Bishop(color: Color, field: ChessField) extends Figure(color, "bishop", field) {
  private val x: Int = field.getX
  private val y: Int = field.getY
  
  override def getAccessibleFields: List[ChessField] = {
    val fields = ListBuffer[ChessField]()

    for (i <- 1 until 8 if x + i < 8 && y + i < 8) {
      if (addField(x + i, y + i, fields)) return fields.toList
    }

    for (i <- 1 until 8 if x + i < 8 && y - i >= 0) {
      if (addField(x + i, y - i, fields)) return fields.toList
    }

    for (i <- 1 until 8 if x - i >= 0 && y + i < 8) {
      if (addField(x - i, y + i, fields)) return fields.toList
    }

    for (i <- 1 until 8 if x - i >= 0 && y - i >= 0) {
      if (addField(x - i, y - i, fields)) return fields.toList
    }

    fields.toList
  }


  private def addField(x: Int, y: Int, fields: ListBuffer[ChessField]): Boolean = {
    val fieldOption = Option(this.field.getBoard.getField(x, y))
    fieldOption match {
      case Some(field) =>
        if (field.getFigure == null) {
          fields += field
          false
        } else if (field.getFigure.color != color) {
          fields += field
          true
        } else {
          true
        }
      case None => true
    }
  }
}
