package Project.figure

import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.DataFormat
import Project.{ChessField, ChessGame, Helper}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

abstract class Figure(val color: Color, val name: String, private var field: ChessField) extends Serializable {

  private val imageFileName = s"images/${color.name}_${name}.png"
  private var firstTurn: Int = -1
  private var x: Int = -1
  private var y: Int = -1

  initField(field)

  private def initField(field: ChessField): Unit = {
    if (field != null) {
      this.field = field
      this.x = field.getX
      this.y = field.getY
      field.setFigure(this, graphic = true)
    }
  }

  def getX: Int = x
  def getY: Int = y
  def getColor: Color = color
  def getName: String = name
  def getField: ChessField = field
  def getFirstTurn: Int = firstTurn
  def setFirstTurn(turn: Int): Unit = firstTurn = turn

  def getPos: String = s"${(x + 97).toChar}${(7 - y + 49).toChar}"

  def move(targetField: ChessField, graphic: Boolean): Figure = {
    val killedFigure = targetField.getFigure
    targetField.setFigure(this, graphic)
    if (field != null) field.setFigure(null, graphic)
    val oldX = x
    val oldY = y
    x = targetField.getX
    y = targetField.getY
    setField(targetField)
    val postFigure = postTurnAction(ChessGame.getBoard.getField(oldX, oldY), targetField, graphic)
    if (graphic && firstTurn < 0) firstTurn = targetField.getBoard.getCurrentTurn
    if (graphic && (killedFigure != null || !postFigure.isInstanceOf[Pawn])) {
      ChessGame.getBoard.set50MoveRuleTurns(0)
    }
    Option(killedFigure).getOrElse(postFigure)
  }

  def canMoveTo(targetField: ChessField): Boolean = canMove && getAllAccessibleFields.contains(targetField)

  def canMove: Boolean = getField.getBoard.getTurn == color

  def setField(field: ChessField): Unit = this.field = field

  def getImageView: ImageView = new ImageView(Figure.imageCache(imageFileName))

  def getImage: Image = Figure.imageCache(imageFileName)

  def postTurnAction(oldField: ChessField, newField: ChessField, graphic: Boolean): Figure = null

  def getAllAccessibleFields: List[ChessField] = {
    import scala.jdk.CollectionConverters._

    val fields = getAccessibleFields().asScala // Convert Java list to Scala list
    val king = field.getBoard.getKing(color)
    if (king != null) {
      fields.filter { to =>
        val oldField = field
        val killedFigure = move(to, graphic = false)
        field.getBoard.recalculateAttackedFields()
        val check = king.isCheck
        move(oldField, graphic = false)
        if (killedFigure != null) killedFigure.field.setFigure(killedFigure, graphic = false)
        field.getBoard.recalculateAttackedFields()
        !check
      }.toList
    } else fields.toList
  }

  override def toString: String = s"Figure:<${color.name} $name x=$x y=$y Field=$field>"

  def getAccessibleFields(): java.util.List[ChessField]

}

object Figure {
  private val imageCache: mutable.Map[String, Image] = mutable.Map.empty

  val CHESS_FIGURE: DataFormat = new DataFormat("chess.figure") // Move CHESS_FIGURE here

  def loadImage(resource: String): Image =
    imageCache.getOrElseUpdate(resource, Helper.loadImage(resource, 50, 50))
}

