package org.fancypoi.excel

import org.apache.poi.ss.usermodel.Sheet
import FancyExcelUtils._
import org.fancypoi.Implicits._

/**
 * Wrapper for Excel Sheet, import Implicits for convenience
 * User: ishiiyoshinori
 * Date: 11/05/04
 */

class FancySheet(protected[fancypoi] val _sheet: Sheet) {
  lazy val workbook = _sheet.getWorkbook

  override def toString = "#" + _sheet.getSheetName

  def cell(address: String): FancyCell = {
    val (colIndex, rowIndex) = addrToIndexes(address)
    cellAt(colIndex, rowIndex)
  }

  def cellAt(colIndex: Int, rowIndex: Int): FancyCell = {
    rowAt(rowIndex).cellAt(colIndex)
  }

  def cell_?(address: String): Option[FancyCell] = {
    val (colIndex, rowIndex) = addrToIndexes(address)
    cellAt_?(colIndex, rowIndex)
  }

  def cellAt_?(colIndex: Int, rowIndex: Int): Option[FancyCell] = {
    rowAt_?(rowIndex).flatMap(r ⇒ r.cellAt_?(colIndex))
  }

  def row(address: String): FancyRow = rowAt(address.toInt - 1)

  def rowAt(index: Int): FancyRow = rowAt_?(index) match {
    case Some(row) ⇒ row
    case None ⇒ _sheet.createRow(index)
  }

  def row_?(address: String): Option[FancyRow] = rowAt_?(address.toInt - 1)

  def rowAt_?(index: Int): Option[FancyRow] = Option(new FancyRow(_sheet.getRow(index)))

  def rows: List[FancyRow] = (0 to lastRowIndex).map(rowAt).toList

  def rows(rowRange: (String, String)): List[FancyRow] = {
    val startIndex = rowRange._1.toInt - 1
    val endIndex = rowRange._2.toInt - 1
    (startIndex to endIndex).map(rowAt).toList
  }

  def rowsAt(rowIndexRange: (Int, Int)) = {
    (rowIndexRange._1 to rowIndexRange._2).map(rowAt).toList
  }

  def insertRows(rowAddr: String, num: Int) = insertRowsAt(rowAddr.toInt - 1, num)

  /**
   * Inserts Rows and shift following rows down correctly.
   *
   * @param insertRowIndex
   * @param num
   */
  def insertRowsAt(insertRowIndex: Int, num: Int) {
    if (0 < num) {
      val endRowIndex = _sheet.getLastRowNum < insertRowIndex match {
        case true ⇒ insertRowIndex
        case false ⇒ _sheet.getLastRowNum
      }
      _sheet.shiftRows(insertRowIndex, endRowIndex, num, true, true)
    }
  }

  /**
   * This looks like it should be getFirstRowNum not TopRow . TopRow is visible row.
   * So I changed it since I like dealing with a sheet regardless of hidden rows/columns etc.
   * @return
   */
  def firstRowIndex = _sheet.getFirstRowNum

  def firstRowAddr: String = (_sheet.getFirstRowNum + 1).toString

  def lastRowIndex = _sheet.getLastRowNum

  def lastRowAddr: String = (_sheet.getLastRowNum + 1).toString

  /**
   * Removes a row and moves the other rows up?  This may be buggy,
   * but also triggers errors when updating row formula etc.
   *
   * @param row
   */
  def removeRow(row: FancyRow) {
    val indx = row.index
    _sheet.removeRow(row) // TODO: This may not shift the rows up and leave an "empty" row
    //		val start = indx +1
    //		val end = this.getLastRowNum
    //		if (start < end)	_sheet.shiftRows(indx+1, this.getLastRowNum , -1)
  }
}
