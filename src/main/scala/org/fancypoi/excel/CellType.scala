package org.fancypoi.excel

/**
 * Cell の static final なフィールドにアクセスできないので移植した。
 * Kind of want to make an enumeration
 */
object CellType {
  final val CELL_TYPE_NUMERIC: Int = 0
  final val CELL_TYPE_STRING: Int = 1
  final val CELL_TYPE_FORMULA: Int = 2
  final val CELL_TYPE_BLANK: Int = 3
  final val CELL_TYPE_BOOLEAN: Int = 4
  final val CELL_TYPE_ERROR: Int = 5

  def humanize(cellType: Int) = cellType match {
    case 0 ⇒ "CELL_TYPE_NUMERIC"
    case 1 ⇒ "CELL_TYPE_STRING"
    case 2 ⇒ "CELL_TYPE_FORMULA"
    case 3 ⇒ "CELL_TYPE_BLANK"
    case 4 ⇒ "CELL_TYPE_BOOLEAN"
    case 5 ⇒ "CELL_TYPE_ERROR"
    case _ ⇒ "!UNKNOWN_CELL_TYPE"
  }
}

object FancyCellType extends Enumeration {
  type FancyCellType = Value
  val NUMERIC_CELL = Value(0, "NUMERIC")
  val STRING_CELL = Value(1, "STRING")
  val FORMULA_CELL = Value(2, "FORMULA")
  val BLANK_CELL = Value(3, "BLANK")
  val BOOLEAN_CELL = Value(4, "BOOLEAN")
  val ERROR_CELL = Value(5, "ERROR")

}
