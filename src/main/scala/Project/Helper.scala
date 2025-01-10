package Project

import javafx.scene.image.Image

import java.io.{File, IOException}
import java.nio.file.{Files, Paths}
import java.net.{URI, URL}

object Helper {

  def loadImage(resource: String, width: Int, height: Int): Image = {
    val stream = Option(getClass.getClassLoader.getResourceAsStream(resource))
    stream match {
      case Some(s) => new Image(s, width, height, true, true)
      case None =>
        throw new IllegalArgumentException(s"Resource $resource not found.")
    }
  }

  def loadDataFromFile(file: File): Option[Array[Byte]] = {
    try {
      Some(Files.readAllBytes(Paths.get(file.toURI)))
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
        None
    }
  }

  def loadDataFromResource(resource: String): Option[Array[Byte]] = {
    val uri: Option[URI] = try {
      Option(getClass.getClassLoader.getResource(resource)).map(_.toURI)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        None
    }

    uri.flatMap { u =>
      try {
        Some(Files.readAllBytes(Paths.get(u)))
      } catch {
        case ex: IOException =>
          ex.printStackTrace()
          None
      }
    }
  }

  def saveDataToFile(data: Array[Byte], file: File): Unit = {
    try {
      Files.write(Paths.get(file.toURI), data)
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
    }
  }
}
