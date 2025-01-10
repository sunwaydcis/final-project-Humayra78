package Project.figure

import java.io.Serializable

enum Color(val name: String) extends Serializable {

  case BLACK extends Color("black")
  case WHITE extends Color("white")

  def getName: String = name

  def getFancyName: String = name.capitalize

  def revert: Color = this match {
    case BLACK => WHITE
    case WHITE => BLACK
  }
}
