package Project.figure

import Project.ChessField
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class Knight(color: Color, field: ChessField) extends Figure(color, "knight", field) {

  def x: Int = field.getX
  def y: Int = field.getY

  override def getAccessibleFields(): java.util.List[ChessField] = {
    val fields = ListBuffer[ChessField]()

    addField(x + 2, y + 1, fields)
    addField(x + 2, y - 1, fields)
    addField(x + 1, y + 2, fields)
    addField(x + 1, y - 2, fields)
    addField(x - 2, y + 1, fields)
    addField(x - 2, y - 1, fields)
    addField(x - 1, y + 2, fields)
    addField(x - 1, y - 2, fields)

    fields.toList.asJava
  }

  private def addField(x: Int, y: Int, fields: ListBuffer[ChessField]): Unit = {
    val targetField = field.getBoard.getField(x, y)
    if (targetField != null && (targetField.getFigure == null || targetField.getFigure.color != color)) {
      fields += targetField
    }
  }
}
