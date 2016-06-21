package com.odenzo.utils.excel

import java.util.Date

import scala.util.Try

import org.apache.poi.ss.usermodel._


trait ExcelCellParsers {

  /**
    * Gets the value of a cell as a String
    *
    * @param cell to get the content of, POI Cell now, not sure if FancyPOI cell is coerced
    *
    * @return Cell content in text format, including result of formula if a String
    */
  def cellContentString(cell: Cell): String = {
    if (cell == null) return ""
    val x = cell.getRichStringCellValue.toString
    if (x == null) return ""
    x.trim
  }


  /**
    * This doesn't handle all cases, e..g Double / Boolean yet. Needs testing and refinement
    * Might as well just smartContent.toString it eh?
    *
    * @param cell
    *
    * @return
    */
  def cellContentOptString(cell: Cell): Option[String] = {
    // Not sure the best pattern to use for these scenarios
    Try[String] {
      val text = cell.getRichStringCellValue.toString.trim
      if (text.length < 1) throw new IllegalStateException("Empty")
      else text
    }.toOption
  }

  /**
    * Crude start at a smart Cell Value thing.
    * Note this doesn't handle formula or date values well
    * Not sure how to get the formula result type
    *
    * @param cell Cell, null allowed, to get the content value of by heuristic
    *
    * @return
    */
  def cellContentSmart(cell: Cell): Option[Any] = {
    if (cell == null) return None
    cell.getCellType match {
      case 0 ⇒ Some(cell.getNumericCellValue) // "CELL_TYPE_NUMERIC"
      case 1 ⇒ Some(cell.getStringCellValue) //"CELL_TYPE_STRING"
      case 2 ⇒ Some(cell.getStringCellValue) // "CELL_TYPE_FORMULA"
      case 3 ⇒ None // "CELL_TYPE_BLANK"
      case 4 ⇒ Some(cell.getBooleanCellValue) // "CELL_TYPE_BOOLEAN"
      case 5 ⇒ None // "CELL_TYPE_ERROR"
      case _ ⇒ Some(cell.getStringCellValue) // "!UNKNOWN_CELL_TYPE"
    }
  }

}

/**
  * Some functions, which may just forward ...
  **/
trait ExcelFns extends ExcelCellParsers {

  import scala.collection.JavaConverters._

  import org.apache.poi.ss.usermodel.Row

  private val alphabets       = ('A' to 'Z').toList
  private val alphabetIndexes = Map(alphabets.zipWithIndex: _*)


  def cellsUntil(r: Row)(badCellFn: (Cell) ⇒ Boolean): Seq[Cell] = {
    r.cellIterator().asScala.takeWhile(c ⇒ badCellFn(c) != true).toSeq
  }

  def filteredCells(r: Row)(filterFn: (Cell) ⇒ Boolean): Seq[Cell] = {
    r.cellIterator().asScala.toSeq.filter(filterFn)
  }


  /**
    *
    * @param rowIndex Starts at row 0
    * @param colIndex Starts at col A=0
    *
    * @return Converts to Excel address like AA23
    */
  def cellindexesToAddr(rowIndex: Int, colIndex: Int): String = {
    colIndexToAddr(colIndex) + rowIndex
  }


  /**
    * 列アドレスを列インデックスに変換します。
    * To convert the column address to the column index.
    * Address assumed to be upper case, e.g. AB
    * e.g. A -> 0
    */
  def colAddrToIndex(col: String) = {
    // col.toUpperCase? Slow and safe
    col.toList.reverse.zipWithIndex.foldLeft(0) {
      case (n, (alphabet, index)) =>
        val base: Int = n + scala.math.pow(26, index).toInt
        val v = base * (
          alphabetIndexes(alphabet) + {
            if (0 < index) 1 else 0 // To deal with ???
          }
          )
        v
    }
  }

  /**
    * 列インデックスを列アドレスに変換します。
    * FIXME: Warning: This only handles A -> ZZ   What is biggest column in Excel?
    */
  def colIndexToAddr(index: Int) = {
    require(index > 0, "Column Index Must be greater than zero ")
    index / 26 match {
      case 0     ⇒ alphabets(index % 26).toString
      case count ⇒ alphabets(count - 1).toString + alphabets(index % 26).toString
    }
  }

  /**
    * TODO NOTE: Worth making a case class ExcelAddress(colAddr, row)?
    *
    * @param address Expects upper case column address.
    *
    * @return Indexes, which are 0 based, address are one based. Would like to just keep everything one based.
    */
  def addrToIndexes(address: String) = {
    val m = "([A-Z]+)(\\d+)".r.findAllIn(address).matchData.toList(0)
    val colAddr = m.group(1)
    val rowAddr = m.group(2)
    (colAddrToIndex(colAddr), rowAddr.toInt - 1)
  }


  /**
    * Unhides all sheets in a workbook.
    *
    * @param workbook
    */
  def unhideAllSheets(workbook: Workbook): Unit = {
    (0 until workbook.getNumberOfSheets).foreach { indx ⇒
      workbook.setSheetHidden(indx, false)
    }
  }

  /**
    * @return True if the row has zero height, which indicates it is hidden
    */
  def rowIsHidden(row: Row) = row.getZeroHeight

  /**
    * Determines if a Excel row is empty by checking that the first cell is present and has some data.
    * Note this is not generically true, as first cell could be blank
    *
    * @param row
    *
    * @return True if the row has some content, base definition row not null and ...
    */
  def rowNotEmpty(row: Row): Boolean = {
    if (row == null) return false
    cellNotEmpty(row.getCell(0))
  }

  /**
    * Determines if a Excel row is empty by checking that the first cell is present and has some data.
    *
    * @param row
    *
    * @return True if the row has some content, base definition row not null and ...
    */
  def rowIsEmpty(row: Row): Boolean = !rowNotEmpty(row)

  def cellNotEmpty(cell: Cell): Boolean = cellContentSmart(cell).isDefined


  /**
    * Maps column titles to rowIndex starting at RowIndex(0)
    *
    * @param titles
    * @param sheet
    * @param style
    */
  def writeColumnTitles(titles: Seq[String], sheet: Sheet, style: Option[CellStyle]) {
    val row = sheet.createRow(0)
    val cells = Range(0, titles.size).map(row.createCell)
    if (style.isDefined) {
      cells.foreach(_.setCellStyle(style.get))
    }
    cells.zip(titles).foreach(x ⇒ {
      x._1.setCellValue(x._2)
    }
    )

    // Could add formatting here if not so lazy
  }

  /**
    * Creates new row for each title starting at the second row (skipping top row)
    *
    * @param titles
    * @param sheet
    */
  def writeRowTitles(titles: Seq[String], sheet: Sheet) {

    val cells = Range(1, titles.size).map(sheet.createRow(_).createCell(0))
    cells.zip(titles).foreach(x ⇒ x._1.setCellValue(x._2))

    // Could add formatting here if not so lazy
  }

  /**
    * Assuming a blank existing sheet, this creates a rudimentory Revisions template populating the first line.
    * Bad form as creating multiple styles. TODO: Seperate out all Style creation
    *
    * @param sheet
    * @param author
    * @param changes
    * @param date
    */
  def writeRevisionsSheet(sheet: Sheet, author: String, changes: String, date: Date) {
    val wb = sheet.getWorkbook
    val headerStyle = {
      val s = wb.createCellStyle()
      s.setBorderBottom(CellStyle.BORDER_MEDIUM)
      s.setAlignment(CellStyle.ALIGN_CENTER)
      s.setFillPattern(CellStyle.SOLID_FOREGROUND)
      s.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index)
      s
    }

    writeColumnTitles(Seq("Revision", "Author", "Changes", "Date"), sheet, Some(headerStyle))
    val row = sheet.createRow(2)
    val data = Seq[AnyRef]("V1.0", author, changes, date).zip(Range(0, 4))

    row.createCell(0).setCellValue("V1.0")
    row.createCell(1).setCellValue(author)

  }

  /**
    * TODO: WIP, Cells have values and types.
    *
    * @param c
    * @param ov
    */
  def writeCell(c: Cell, ov: Option[Any]) {
    ov match {
      case None                        ⇒ // c.setCellType()// Set the cell to Blank/Empty
      case Some(v: String)             ⇒ c.setCellValue(v)
      case Some(v: RichTextString)     ⇒ c.setCellValue(v)
      case Some(v: Boolean)            ⇒ c.setCellValue(v)
      case Some(v: Double)             ⇒ c.setCellValue(v)
      case Some(v: Date)               ⇒ c.setCellValue(v)
      case Some(v: java.util.Calendar) ⇒ c.setCellValue(v)
      case Some(v)                     ⇒ c.setCellValue(v.toString)
    }
  }


  def autosizeColumnsToMaxWidth(sheet: Sheet, maxWidthInSizeOfFirstFont: Double, cols: Range) {
    val maxSize: Int = (maxWidthInSizeOfFirstFont * 256).toInt // POI Excel Genius
    cols.foreach { col ⇒
      sheet.autoSizeColumn(col)
      if (sheet.getColumnWidth(col) > maxSize) sheet.setColumnWidth(col, maxSize)
    }
  }
}
