package Project

trait ChessIO {

  def load(data: Array[Byte], board: ChessBoard): Unit

  def save(board: ChessBoard): Array[Byte]

  def getFileTypeDescription: String

  def getFileExtension: String
}
