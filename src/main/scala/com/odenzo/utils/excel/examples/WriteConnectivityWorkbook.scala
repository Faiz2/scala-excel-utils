package com.odenzo.utils.excel.examples

import java.io.File

import com.odenzo.utils.excel.ExcelFns
import com.odenzo.utils.excel.StdExcelStyles
import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.fancypoi.Implicits._
import org.fancypoi.excel._

/**
 * This handles all the Excel and templating stuff.
 * Uses functions to pull the list of applications (or App Labels) and the connectivity.
 */
class WriteConnectivityWorkbook() extends LazyLogging with StdExcelStyles with ExcelFns {

  /**
   * Function to get connectivity from Appliation A to Application B
   */
  val onlineCount: (String, String) => Option[String] = (s: String, d: String) => None

  val batchCount: (String, String) => Option[String] = onlineCount

  /**
   * Subclasses expected to override this. I like that better than adding in constructor now-a-days.
   * Subclasses can put in their constructor if they want.
   */
  val apps: Seq[String] = Seq()

  val wb: FancyWorkbook = {
    val sheetNames: Seq[String] = Seq("Online Connectivity", "Batch Conenctivity", "Meta-Data")
    createWorkbookWithSheet(sheetNames)
  }

  //<editor-fold desc="Fonts and Styles">

  // This style stuff is verbose.

  // Example of Overriding standard styles, need to check the ordering of val constructions. Chained stuff will break?
  override lazy val stdFont = wb.getFontWith(_.setFontHeightInPoints(14))

  val headerFont = wb.getFontBasedWith(stdFont)(_.setBoldweight(600))

  // Or you can just extend on the standard styles.
  val styleDefault = wb.getStyleBasedWith(stdStyle)(s => {
    s.setAlignment(CellStyle.ALIGN_CENTER)
    s.setFont(stdFont)
  })

  // Blah, this is the correct syntax, TODO: Update others to sane syntax
  val styleNoConnection = wb.getStyle {
    s =>
      s.setAlignment(CellStyle.ALIGN_CENTER)
      s.setFont(stdFont)
  }

  val styleConnection = wb.getStyleBasedWith(styleNoConnection) {
    s =>
      s.setFillForegroundColor(IndexedColors.GREEN.index)
      s.setFillPattern(CellStyle.SOLID_FOREGROUND)

  }

  val styleConnectionNoCount = wb.getStyleBasedWith(styleNoConnection) {
    s => s.setFillForegroundColor(IndexedColors.YELLOW.index)
  }

  val styleRowHeader = wb.getStyleBasedWith(styleDefault) {
    // Forget what I was thinking about here.
    case (style) =>
      style.setAlignment(CellStyle.ALIGN_LEFT)
      style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
      style.setFillPattern(CellStyle.SOLID_FOREGROUND)
      style.setIndention(2)
  }

  val styleColSum = wb.getStyleBasedWith(styleDefault)(s => {
    s.setAlignment(CellStyle.ALIGN_RIGHT)
    s.setFillForegroundColor(IndexedColors.TAN.index)
    s.setFillPattern(CellStyle.SOLID_FOREGROUND)
    s.setBorderTop(CellStyle.BORDER_THICK)

  })
  val styleRowSum = wb.getStyleBasedWith(styleColSum)(s => {
    s.setBorderLeft(CellStyle.BORDER_NONE)
    s.setBorderLeft(CellStyle.BORDER_THICK)

  })

  //</editor-fold>

  def populateConnectivityMatrices() {
    writeConnectivitySheet(wb.sheetAt(0), apps, onlineCount)
    writeConnectivitySheet(wb.sheetAt(1), apps, batchCount)
  }

  def write(f: File) {
    wb.write(f)
  }

  protected def writeConnectivitySheet(sheet: Sheet, appLabels: Seq[String], countFun: (String, String) => Option[String]) {

    writeColumnTitles("Row To -> Col From" +: appLabels, sheet, Some(stdColHeaderRotatedStyle))
    // TODO: May have to fix the very first cell which we really want different style on

    val rows = Range(1, apps.size + 1).zip(apps)
    val cols = rows // Copy of rows basically, since symmetric matrix

    for ((rowNum, srcApp) <- rows) {
      val row: FancyRow = sheet.createRow(rowNum)

      val rowHCell = row.createCell(0)
      rowHCell.setCellValue(srcApp)
      rowHCell.setCellStyle(styleRowHeader)

      for ((colNum, destApp) <- cols) {
        val cell: FancyCell = row.createCell(colNum)

        countFun(srcApp, destApp) match {
          case None =>
            cell.replaceStyle(styleNoConnection); cell.value("-"): Unit
          case Some("??") =>
            cell.value("??"); cell.setCellStyle(styleConnectionNoCount): Unit
          case Some(count) => cell.value(count.toInt); cell.replaceStyle(styleConnection): Unit
        }
      }

      val sumRowCell = row.createCell(cols.size + 1)
      val startAddr = s"B${sumRowCell.rowAddr}"
      val endAddr = cellindexesToAddr(sumRowCell.rowAddr, cols.size)
      sumRowCell.formula(s"SUM($startAddr:$endAddr)")
      sumRowCell.setCellStyle(styleRowSum)
    }

    // Now we add a bottom row with SUM(..) formulas, including summing the row sums. {
    val rowNum = apps.size + 1
    val row = sheet.createRow(rowNum)
    for (colIndex <- Range(1, rowNum + 1)) {
      val colName = FancyExcelUtils.colIndexToAddr(colIndex)
      val c = row.createCell(colIndex)
      c.setCellFormula(s"SUM(${colName}2:$colName$rowNum)")
      c.setCellStyle(styleColSum)
    }

    //sheet.setAutoFilter(CellRangeAddress.valueOf("C5:F200"));
    sheet.setZoom(6, 4)
    //sheet.setSelected(true)
    //sheet.createFreezePane(0, 1, 0, 1);
    Range(0, apps.size + 1).foreach(sheet.autoSizeColumn(_))

  }
}
