package Project.figure

import Project.ChessField

import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer

class Queen(color: Color, field: ChessField) extends Figure(color, "queen", field) {

  override def getAccessibleFields(): java.util.List[ChessField] = {
    val fields = ListBuffer[ChessField]()
    val board = field.getBoard

    // Define movement patterns for the queen
    val directions = Seq(
      (1, 1),  // Diagonal up-right
      (1, -1), // Diagonal down-right
      (-1, 1), // Diagonal up-left
      (-1, -1),// Diagonal down-left
      (1, 0),  // Horizontal right
      (-1, 0), // Horizontal left
      (0, 1),  // Vertical up
      (0, -1)  // Vertical down
    )

    // Traverse each direction
    for ((dx, dy) <- directions) {
      var step = 1
      var continue = true
      while (continue) {
        val nx = x + step * dx
        val ny = y + step * dy
        if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
          continue = addField(nx, ny, fields)
        } else {
          continue = false
        }
        step += 1
      }
    }

    fields.toList.asJava
  }

  private def addField(x: Int, y: Int, fields: ListBuffer[ChessField]): Boolean = {
    val targetField = field.getBoard.getField(x, y)
    if (targetField != null) {
      if (targetField.getFigure == null) {
        fields += targetField
        true // Continue exploring this direction
      } else if (targetField.getFigure.color != color) {
        fields += targetField
        false // Stop exploration in this direction after capturing
      } else {
        false // Stop exploration in this direction due to blocking piece
      }
    } else {
      false // Invalid field
    }
  }
}