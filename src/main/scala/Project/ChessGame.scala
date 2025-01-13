package Project

import javafx.application.Application
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.{BorderPane, GridPane}
import javafx.stage.{FileChooser, Stage}
import javafx.stage.FileChooser.ExtensionFilter
import Project.OptionButton

import java.io.File
import java.util.ServiceLoader
import scala.jdk.CollectionConverters.*

class ChessGame extends Application {

  private var status: Label = _
  private var board: ChessBoard = _
  private var godmode: Boolean = false

  override def start(primaryStage: Stage): Unit = {
    ChessGame.instance = this
    godmode = getParameters.getUnnamed.contains("--godmode")

    primaryStage.setTitle("Chess")
    primaryStage.getIcons.add(Helper.loadImage("images/icon.png", 16, 16))

    // Main BorderPane
    val pane = new BorderPane()

    // Chess board with column and row markings
    val table = new GridPane()
    for (i <- 0 until 8) {
      table.add(newRowLabel(i), 0, i + 1, 1, 1)
      table.add(newRowLabel(i), 9, i + 1, 1, 1)
      table.add(newColLabel(i), i + 1, 0, 1, 1)
      table.add(newColLabel(i), i + 1, 9, 1, 1)
    }
    board = new ChessBoard()
    table.add(board, 1, 1, 8, 8)
    table.setAlignment(Pos.CENTER)
    pane.setCenter(table)

    // Menu on the bottom with status text and option buttons
    val menu = new BorderPane()
    menu.setPadding(new Insets(10, 10, 10, 0))

    // Option buttons
    val options = new GridPane()
    options.setAlignment(Pos.BOTTOM_RIGHT)

    // Secret option buttons
    if (godmode) {
      options.add(new OptionButton("images/clear.png", _ => board.clear(), "Clear"), 0, 0, 1, 1)
      options.add(new OptionButton("images/swap.png", _ => board.nextTurn(), "Skip turn"), 1, 0, 1, 1)
    }
    options.add(new OptionButton("images/reset.png", _ => {
      board.clear()
      board.loadFromResource("init.json")
    }, "Reset"), 2, 0, 1, 1)
    options.add(new OptionButton("images/save.png", _ => {
      val file = createFileChooser().showSaveDialog(primaryStage)
      if (file != null) board.save(file)
    }, "Save"), 3, 0, 1, 1)
    options.add(new OptionButton("images/load.png", _ => {
      val file = createFileChooser().showOpenDialog(primaryStage)
      if (file != null) board.load(file)
    }, "Load"), 4, 0, 1, 1)
    menu.setRight(options)

    // Status text
    status = new Label()
    status.setAlignment(Pos.BOTTOM_LEFT)
    status.setPadding(new Insets(10, 0, 10, 10))
    menu.setLeft(status)

    pane.setBottom(menu)

    // Scene
    val scene = new Scene(pane, 440, 490)
    primaryStage.setScene(scene)
    primaryStage.show()
    primaryStage.setMinWidth(primaryStage.getWidth)
    primaryStage.setMinHeight(primaryStage.getHeight)

    // Set IO and load initial setup
    ServiceLoader.load(classOf[ChessIO]).forEach(board.setIO)
    board.loadFromResource("init.json")
  }

  private def newRowLabel(i: Int): Label = {
    val l = new Label((8 - i).toString)
    l.setMinSize(20, 50)
    l.setAlignment(Pos.CENTER)
    l
  }

  private def newColLabel(i: Int): Label = {
    val l = new Label((i + 65).toChar.toString)
    l.setMinSize(50, 20)
    l.setAlignment(Pos.CENTER)
    l
  }

  private def createFileChooser(): FileChooser = {
    val fileChooser = new FileChooser()
    board.getIO.foreach { case (extension, ioInstance) =>
      fileChooser.getExtensionFilters.add(new ExtensionFilter(ioInstance.getFileTypeDescription, s"*.${ioInstance.getFileExtension}"))
    }
    fileChooser
  }
}

object ChessGame {
  private var instance: ChessGame = _

  def displayStatusText(text: String): Unit = {
    instance.status.setText(text)
  }

  def isGodmode: Boolean = instance.godmode

  def getBoard: ChessBoard = instance.board
}
