package org.fancypoi.excel

import org.apache.poi.ss.usermodel.{ Row, Cell }
import FancyExcelUtils._
import org.fancypoi.Implicits._

class FancyRow(protected[fancypoi] val _row: Row) {

  override def toString = "#" + _row.getSheet.getSheetName + "!*" + addr

  def index = _row.getRowNum
  def addr: String = (_row.getRowNum + 1).toString

  /**
   * Hide this row
   */
  def hide() { _row.setZeroHeight(true) }

  def cell(address: String): FancyCell = cellAt(colAddrToIndex(address))

  /**
   * Gets cell at index, if no cell returns a newly created blank cell.
   * @param index   zero based column index
   * @return
   */
  def cellAt(index: Int): FancyCell = _row.getCell(index, Row.CREATE_NULL_AS_BLANK)

  def cell_?(address: String) = cellAt_?(colAddrToIndex(address))

  def cellAt_?(index: Int) = Option(_row.getCell(index, Row.RETURN_NULL_AND_BLANK): FancyCell)

  def cells: List[FancyCell] = (0 to lastColIndex).map(cellAt).toList

  def firstColAddr: String = colIndexToAddr(firstColIndex)

  def firstColIndex: Int = _row.getFirstCellNum.toInt

  def lastColAddr: String = colIndexToAddr(lastColIndex)

  def lastColIndex: Int = _row.getLastCellNum.toInt

  def cellsFrom(startColAddr: String)(block: CellSeq => Unit) {
    cellsFromAt(colAddrToIndex(startColAddr))(block)
  }

  def cellsFromAt(startColIndex: Int)(block: CellSeq => Unit) {
    block(new CellSeq(_row, startColIndex))
  }

  private class CellSeq(row: Row, colIndex: Int) {
    var current = colIndex

    def apply(block: Cell => Unit) {
      block(row.cellAt(current))
      current += 1
    }
  }

}
