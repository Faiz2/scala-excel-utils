package com.odenzo.utils.excel

import java.util.Date

import org.apache.poi.ss.usermodel._
import org.fancypoi.Implicits._
import org.fancypoi.excel.FancyCell
import org.fancypoi.excel.{FancyExcelUtils, FancyRow, FancySheet}



trait ExcelCellParsers {

 /**
    * Get the value of a cell suitable for storing in Neo4J.
    *
    * @param cell to get the content of, POI Cell now, not sure if FancyPOI cell is coerced
    *
    * @return
    */
  def cellContent(cell: FancyCell): String = {
    if (cell == null) return ""

    val x = cell.getRichStringCellValue.toString
    if (x == null) return ""
    x.trim
  }

  /**
    * Trying to decide how to organize cell parsing.
    * If blank or null cell then None, else if a number (of any kind?) then value.
    * But what if error, i.e. it is a String cell with value "Cat"
    * If it is a String cell that is parseable into an Integer no problem, same with RichText.
    * What if it is a Date cell, still return the Int representation I think.
    *
    * @param cell
    *
    * @return
    */
  def contentAsInt(cell: Cell): Option[Long] = {
    None
  }

  /**
    * Crude start at a smart Cell Value thing. I think a FancyCellValue class a good idea.
    *
    * @param cell
    *
    * @return
    */
  def smartContent(cell: FancyCell): Option[Any] = {
    if (cell == null) return None
    cell.getCellType match {
      case 0 ⇒ Some(cell.numericValue) // "CELL_TYPE_NUMERIC"
      case 1 ⇒ Some(cell.stringValue) //"CELL_TYPE_STRING"
      case 2 ⇒ Some(cell.stringValue) // "CELL_TYPE_FORMULA"
      case 3 ⇒ None // "CELL_TYPE_BLANK"
      case 4 ⇒ Some(cell.booleanvalue) // "CELL_TYPE_BOOLEAN"
      case 5 ⇒ None // "CELL_TYPE_ERROR"
      case _ ⇒ Some(cell.stringValue) // "!UNKNOWN_CELL_TYPE"
    }
  }

}
/**
  * Some functions, which may just forward ...
  **/
trait ExcelFns extends ExcelCellParsers {

  import org.apache.poi.ss.usermodel.Row
  import org.fancypoi.excel.{FancyCell, FancyWorkbook}

  /**
    *
    * @param rowIndex Starts at row 0
    * @param colIndex Starts at col A=0
    *
    * @return Converts to Excel address like AA23
    */
  def cellindexesToAddr(rowIndex: Int, colIndex: Int): String = {
    FancyExcelUtils.colIndexToAddr(colIndex) + rowIndex
  }

  /**
    *
    *
    * @return Converts Excel Column like A -> 0 or AA to 26
    */
  def colAddrToIndex(addr: String): Int = FancyExcelUtils.colAddrToIndex(addr)

  def colIndexToAddr(indx: Int): String ={
    require(indx>0,"Column Index Must be greater than zero ")
    FancyExcelUtils.colIndexToAddr(indx)
  }

  /**
    * Creates a new Xlsx Workbook with the sheet given in sheetnames (in order)
    *
    * @param sheetNames
    *
    * @return
    */
  def createWorkbookWithSheet(sheetNames: Seq[String]): FancyWorkbook = {
    val wb = FancyWorkbook.createXlsx
    sheetNames.foreach(wb.createSheet)
    wb
  }

  /**
    * Unhides all sheets in a workbook.
    *
    * @param workbook
    */
  def unhideAllSheets(workbook: FancyWorkbook) {
    for (sheet ← workbook.sheets) {
      val indx = workbook.getSheetIndex(sheet)
      if (workbook.isSheetHidden(indx)) {
        workbook.setSheetHidden(indx, false)
      }
    }
  }

  /**
    * TODO: Candidate for FancyRow
    *
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
  def rowNotEmpty(row: FancyRow): Boolean = {
    if (row == null) return false
    cellNotEmpty(row.cellAt(0))
  }

  /**
    * Determines if a Excel row is empty by checking that the first cell is present and has some data.
    *
    * @param row
    *
    * @return True if the row has some content, base definition row not null and ...
    */
  def rowIsEmpty(row: FancyRow): Boolean = !rowNotEmpty(row)

  def cellNotEmpty(cell: FancyCell): Boolean = smartContent(cell).isDefined


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
    *
    * @param sheet
    * @param author
    * @param changes
    * @param date
    */
  def writeRevisionsSheet(sheet: Sheet, author: String, changes: String, date: Date) {

    val wb = sheet.workbook
    val headerStyle = {
      val s = wb.createCellStyle()
      s.setBorderBottom(CellStyle.BORDER_MEDIUM)
      s.setAlignment(CellStyle.ALIGN_CENTER)
      s.setFillPattern(CellStyle.SOLID_FOREGROUND)
      s.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT)
      s
    }
    writeColumnTitles(Seq("Revision", "Author", "Changes", "Date"), sheet, Some(headerStyle))
    val row = sheet.createRow(2)
    val data = Seq[AnyRef]("V1.0", author, changes, date).zip(Range(0, 4))

    // Hmm, should write a nice generic writer based on the value Type.

    row.createCell(0).value("V1.0")
    row.createCell(1).value(author)

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

  def autosizeColumnsToMaxWidth(sheet: FancySheet, maxWidthInSizeOfFirstFont: Double, cols: Range) {
    val maxSize: Int = (maxWidthInSizeOfFirstFont * 256).toInt // POI Excel Genius
    cols.foreach {
      col ⇒
        sheet.autoSizeColumn(col)
        if (sheet.getColumnWidth(col) > maxSize) sheet.setColumnWidth(col, maxSize)
    }
  }
}
