package Project

import javafx.scene.control.{ButtonType, Dialog, Label}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import Project.figure._

class PromotionDialog(pawn: Pawn) extends Dialog[Figure] {

  private var selectedFigure: Figure = _

  setTitle(s"Promote Pawn ${pawn.getColor.getName}")
  setResultConverter(_ => selectedFigure)

  private val hbox = new HBox()
  hbox.getChildren.addAll(
    new PromotionCandidateLabel(new Queen(pawn.getColor, null)),
    new PromotionCandidateLabel(new Knight(pawn.getColor, null)),
    new PromotionCandidateLabel(new Rook(pawn.getColor, null)),
    new PromotionCandidateLabel(new Bishop(pawn.getColor, null))
  )
  getDialogPane.setContent(hbox)

  private class PromotionCandidateLabel(val figure: Figure) extends Label {

    setGraphic(figure.getImageView)
    setOnMouseReleased(onMouseReleased)

    private def onMouseReleased(e: MouseEvent): Unit = {
      selectedFigure = figure
      getDialogPane.getButtonTypes.add(ButtonType.CANCEL)
      close()
      e.consume()
    }
  }
}
