package Project

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input._
import Project.figure.{Color, Figure, Pawn}
import java.util.List
import scala.jdk.CollectionConverters._

class ChessField(private val board: ChessBoard, val x: Int, val y: Int) extends Label {

  private var figure: Figure = _
  private val defaultStyleBlack = "-fx-background-color: gray;"
  private val defaultStyleWhite = "-fx-background-color: white;"
  private val highlightStyleBlack = "-fx-background-color: forestgreen;"
  private val highlightStyleWhite = "-fx-background-color: palegreen;"
  private val highlightKillStyleBlack = "-fx-background-color: darkred;"
  private val highlightKillStyleWhite = "-fx-background-color: #ff5555;"

  private val dragViewOffset: Int = {
    val osName = System.getProperty("os.name").toLowerCase
    if (osName.contains("windows")) 25 else 0
  }
  
  def getX: Int = {
    x
  }

  def getY: Int = {
    y
  }

  init()

  private def init(): Unit = {
    setAlignment(Pos.CENTER)
    resetBackgroundColor()
    setOnDragDetected(onDragDetected)
    setOnDragOver(onDragOver)
    setOnDragDropped(onDragDropped)
    setOnDragDone(onDragDone)
    setOnMouseEntered(_ => onMouseEntered())
    setOnMouseExited(_ => onMouseExited())
    if (ChessGame.isGodmode) {
      setContextMenu(new GodmodeMenu(this))
    }
    setMinSize(50, 50)
    setMaxSize(50, 50)
  }

  private def setHighlightEmpty(): Unit = {
    setStyle(if (getColor == Color.BLACK) highlightStyleBlack else highlightStyleWhite)
  }

  private def setHighlightKill(): Unit = {
    setStyle(if (getColor == Color.BLACK) highlightKillStyleBlack else highlightKillStyleWhite)
  }

  private def resetBackgroundColor(): Unit = {
    setStyle(if (getColor == Color.BLACK) defaultStyleBlack else defaultStyleWhite)
  }

  private def isEnPassantField(movingFigure: Figure): Boolean = {
    movingFigure match {
      case pawn: Pawn =>
        val opponent = if (y == 2) board.getField(x, 3).getFigure else board.getField(x, 4).getFigure
        (y == 2 || y == 5) && opponent.isInstanceOf[Pawn] && board.getCurrentTurn - opponent.getFirstTurn == 1
      case _ => false
    }
  }

  private def getColor: Color = {
    if ((x % 2 == 1 && y % 2 == 1) || (x % 2 == 0 && y % 2 == 0)) Color.WHITE else Color.BLACK
  }

  def getBoard: ChessBoard = board

  def setFigure(figure: Figure, graphic: Boolean): Unit = {
    this.figure = figure
    if (graphic) {
      if (figure == null) setGraphic(null)
      else setGraphic(figure.getImageView)
    }
  }

  def getFigure: Figure = figure

  private def onMouseEntered(): Unit = {
    if (figure != null && figure.canMove) {
      val accessibleFields = figure.getAllAccessibleFields.asScala
      accessibleFields.foreach { field =>
        if (field.figure != null || field.isEnPassantField(figure)) field.setHighlightKill()
        else field.setHighlightEmpty()
      }
    }
  }

  private def onMouseExited(): Unit = {
    if (figure != null && figure.canMove) {
      val accessibleFields = figure.getAllAccessibleFields.asScala
      accessibleFields.foreach(_.resetBackgroundColor())
    }
  }

  private def onDragDetected(event: MouseEvent): Unit = {
    if (figure != null && figure.canMove) {
      val accessibleFields = figure.getAllAccessibleFields.asScala
      val dragboard = startDragAndDrop(TransferMode.MOVE)
      dragboard.setDragView(figure.getImage)
      dragboard.setDragViewOffsetX(dragViewOffset)
      dragboard.setDragViewOffsetY(dragViewOffset)

      val content = new ClipboardContent()
      content.put(Figure.CHESS_FIGURE, figure)
      dragboard.setContent(content)

      accessibleFields.foreach { field =>
        if (field.figure != null || field.isEnPassantField(figure)) field.setHighlightKill()
        else field.setHighlightEmpty()
      }
      event.consume()
    }
  }

  private def onDragOver(event: DragEvent): Unit = {
    if (event.getDragboard.hasContent(Figure.CHESS_FIGURE)) {
      event.acceptTransferModes(TransferMode.MOVE)
    }
    event.consume()
  }

  private def onDragDone(event: DragEvent): Unit = {
    val dragboard = event.getDragboard
    if (dragboard.hasContent(Figure.CHESS_FIGURE)) {
      val source = deserializeFigure(dragboard)
      source.getAccessibleFields.asScala.foreach(_.resetBackgroundColor())
    }
    event.consume()
  }

  private def onDragDropped(event: DragEvent): Unit = {
    val dragboard = event.getDragboard
    if (dragboard.hasContent(Figure.CHESS_FIGURE)) {
      var source = deserializeFigure(dragboard)
      source = board.getField(source.getX, source.getY).getFigure
      if (source.canMoveTo(this)) {
        resetBackgroundColor()
        source.getAccessibleFields.asScala.foreach(_.resetBackgroundColor())
        source.move(this, graphic = true)
        board.nextTurn()
      }
    }
    event.consume()
  }

  private def deserializeFigure(dragboard: Dragboard): Figure = {
    val source = dragboard.getContent(Figure.CHESS_FIGURE).asInstanceOf[Figure]
    source.setField(ChessGame.getBoard.getField(source.getX, source.getY))
    source
  }

  override def toString: String = s"<$x,$y>"
}
