package Project

import javafx.scene.layout.GridPane
import Project.figure._
import java.io.File
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, HashMap, HashSet}

class ChessBoard extends GridPane {

  private val fields: Array
  private val attackedFields: mutable.Map[Color, mutable.Set[ChessField]] =
    HashMap(Color.BLACK -> HashSet.empty, Color.WHITE -> HashSet.empty)
  private var currentTurn: Int = 1
  private var ruleOf50: Int = 0
  private val io: mutable.Map[String, ChessIO] = mutable.LinkedHashMap.empty

  init()

  private def init(): Unit = {
    resetAttackedFields()
    for (i <- fields.indices) {
      val x = getX(i)
      val y = getY(i)
      val field = new ChessField(this, x, y)
      add(field, x, y)
      fields(i) = field
    }
    recalculateAttackedFields()
  }

  private def resetAttackedFields(): Unit = {
    attackedFields(Color.BLACK).clear()
    attackedFields(Color.WHITE).clear()
  }

  private def getX(index: Int): Int = index % 8

  private def getY(index: Int): Int = index / 8

  def getField(x: Int, y: Int): ChessField =
    if (x < 0 || x > 7 || y < 0 || y > 7) null else fields(y * 8 + x)

  def setFigure(figure: Figure): Unit =
    getField(figure.getX, figure.getY).setFigure(figure, setAttacked = true)

  def getCurrentTurn: Int = currentTurn

  def setCurrentTurn(turn: Int): Unit = currentTurn = turn

  def get50MoveRuleTurns: Int = ruleOf50

  def set50MoveRuleTurns(turns: Int): Unit = ruleOf50 = turns

  def nextTurn(): Unit = {
    currentTurn += 1
    ruleOf50 += 1
    recalculateAttackedFields()
    gameStateTest()
  }

  def getTurn: Color = if (currentTurn % 2 == 0) Color.BLACK else Color.WHITE

  def gameStateTest(): Unit = {
    val king = getKing(getTurn)
    if (king != null) {
      if (king.isCheck) {
        if (king.isCheckMate) {
          ChessGame.displayStatusText(s"Check mate! ${king.getColor.revert.getFancyName} wins.")
        } else {
          ChessGame.displayStatusText(s"Check! ${king.getColor.getFancyName} has to defend.")
        }
      } else if (king.isStaleMate) {
        ChessGame.displayStatusText(s"Stalemate! ${king.getColor.getFancyName} can't move.")
      } else if (ruleOf50 >= 100) {
        ChessGame.displayStatusText("50-move-rule applies")
      } else {
        ChessGame.displayStatusText("")
      }
    }
  }

  def recalculateAttackedFields(): Unit = {
    resetAttackedFields()
    fields
      .filter(f => f.figure != null && f.figure.getColor == Color.WHITE)
      .foreach(f => attackedFields(Color.WHITE) ++= f.figure.getAccessibleFields)
    fields
      .filter(f => f.figure != null && f.figure.getColor == Color.BLACK)
      .foreach(f => attackedFields(Color.BLACK) ++= f.figure.getAccessibleFields)
  }

  def getAllAccessibleFields(color: Color): Set[ChessField] = attackedFields(color).toSet

  def getKing(color: Color): King =
    fields.collectFirst {
      case field if field.figure.isInstanceOf[King] && field.figure.getColor == color =>
        field.figure.asInstanceOf[King]
    }.orNull

  def getFigures: List[Figure] = getFigures(null)

  def getFigures(color: Color): List[Figure] =
    fields
      .filter(f => f.figure != null && (color == null || f.figure.getColor == color))
      .map(_.figure)
      .toList

  def clear(): Unit = {
    fields.foreach(_.setFigure(null, setAttacked = true))
    recalculateAttackedFields()
    currentTurn = 1
    ruleOf50 = 0
    ChessGame.displayStatusText("")
  }

  def setIO(ioInstance: ChessIO): Unit = io(ioInstance.getFileExtension) = ioInstance

  def getIO: Map[String, ChessIO] = io.toMap

  def loadFromResource(resource: String): Unit =
    load(getFileExtension(resource), Helper.loadDataFromResource(resource))

  def load(file: File): Unit =
    load(getFileExtension(file.getName), Helper.loadDataFromFile(file))

  private def getFileExtension(name: String): String = name.substring(name.lastIndexOf('.') + 1)

  private def load(fileType: String, data: Array[Byte]): Unit = {
    clear()
    io(fileType).load(data, this)
    recalculateAttackedFields()
    gameStateTest()
  }

  def save(file: File): Unit = {
    val data = io(getFileExtension(file.getName)).save(this)
    Helper.saveDataToFile(data, file)
  }
}