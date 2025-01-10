package Project

import Project.Helper
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.{Label, Tooltip}
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent

class OptionButton(
                    imageResource: String,
                    mouseClickedEvent: EventHandler[_ >: MouseEvent],
                    tooltip: String
                  ) extends Label {

  private val icon: ImageView = new ImageView(Helper.loadImage(imageResource, 70, 70))

  init()

  private def init(): Unit = {
    setAlignment(Pos.CENTER)
    resizeIcon(30)
    setGraphic(icon)
    setOnMouseClicked(mouseClickedEvent)
    setOnMouseEntered(onMouseEntered)
    setOnMouseExited(onMouseExited)
    setMinHeight(35)
    setMaxHeight(35)
    setMinWidth(40)
    setMaxWidth(40)
    setTooltip(new Tooltip(tooltip))
  }

  private def onMouseEntered: EventHandler[MouseEvent] = (e: MouseEvent) => {
    resizeIcon(35)
    e.consume()
  }

  private def onMouseExited: EventHandler[MouseEvent] = (e: MouseEvent) => {
    resizeIcon(30)
    e.consume()
  }

  private def resizeIcon(width: Int): Unit = {
    icon.setPreserveRatio(true)
    icon.setFitWidth(width)
  }
}
