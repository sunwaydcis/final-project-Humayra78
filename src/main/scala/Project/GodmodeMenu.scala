package Project

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.{ContextMenu, MenuItem}
import javafx.scene.image.{Image, ImageView}
import Project.figure._
import scala.collection.mutable

class GodmodeMenu(field: ChessField) extends ContextMenu {

  // Add menu items
  getItems.addAll(
    new GodItem("images/remove.png", "Remove Figure", _ => setFigure(null)),
    new GodItem("images/white_queen.png", "Add White Queen", _ => setFigure(new Queen(Color.WHITE, field))),
    new GodItem("images/white_bishop.png", "Add White Bishop", _ => setFigure(new Bishop(Color.WHITE, field))),
    new GodItem("images/white_knight.png", "Add White Knight", _ => setFigure(new Knight(Color.WHITE, field))),
    new GodItem("images/white_rook.png", "Add White Rook", _ => setFigure(new Rook(Color.WHITE, field))),
    new GodItem("images/white_king.png", "Add White King", _ => setFigure(new King(Color.WHITE, field))),
    new GodItem("images/white_pawn.png", "Add White Pawn", _ => setFigure(new Pawn(Color.WHITE, field))),
    new GodItem("images/black_queen.png", "Add Black Queen", _ => setFigure(new Queen(Color.BLACK, field))),
    new GodItem("images/black_bishop.png", "Add Black Bishop", _ => setFigure(new Bishop(Color.BLACK, field))),
    new GodItem("images/black_knight.png", "Add Black Knight", _ => setFigure(new Knight(Color.BLACK, field))),
    new GodItem("images/black_rook.png", "Add Black Rook", _ => setFigure(new Rook(Color.BLACK, field))),
    new GodItem("images/black_king.png", "Add Black King", _ => setFigure(new King(Color.BLACK, field))),
    new GodItem("images/black_pawn.png", "Add Black Pawn", _ => setFigure(new Pawn(Color.BLACK, field)))
  )

  private def setFigure(figure: Figure): Unit = {
    field.setFigure(figure, true)
    field.getBoard.recalculateAttackedFields()
    field.getBoard.gameStateTest()
  }

  // Inner class for menu items
  private class GodItem(resourceIcon: String, text: String, event: EventHandler[ActionEvent]) extends MenuItem {

    setGraphic(new ImageView(GodItem.getCachedIcon(resourceIcon)))
    setText(text)
    setOnAction(event)
  }

  // Companion object for GodItem to cache icons
  private object GodItem {
    private val cachedIcons: mutable.Map[String, Image] = mutable.Map()

    def getCachedIcon(resourceIcon: String): Image = {
      cachedIcons.getOrElseUpdate(resourceIcon, Helper.loadImage(resourceIcon, 16, 16))
    }
  }
}
