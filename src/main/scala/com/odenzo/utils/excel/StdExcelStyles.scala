package com.odenzo.utils.excel

import org.apache.poi.ss.usermodel.{ CellStyle, IndexedColors }
import org.fancypoi.excel.FancyWorkbook

/**
 * Some common styles for writing out Excel sheets. These are not true utilities but time savers for me.
 *
 * @author Steve Franks
 */
trait StdExcelStyles {

  import org.fancypoi.Implicits._

  val wb: FancyWorkbook // Abstract, so must be in the class we are mixing into, no need for implicits (?)

  // wb is not initialized straight away before here, so can make lazy or move to earliy init I guess.
  lazy val stdFont = wb.getFontWith(_.setFontHeightInPoints(14))

  lazy val stdHeaderFont = wb.getFontBasedWith(stdFont)(_.setBoldweight(600))

  lazy val dataFormat = wb.createDataFormat()

  def format(format: String) = dataFormat.getFormat(format)

  lazy val stdDateFormat = format("yyyy-mm-dd")

  lazy val stdStyle = wb.getStyle(s => {
    s.setAlignment(CellStyle.ALIGN_CENTER)
    s.setFont(stdFont)
  })

  lazy val stdRowHeaderStyle = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setAlignment(CellStyle.ALIGN_LEFT)
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
        s.setFillPattern(CellStyle.SOLID_FOREGROUND)
        s.setIndention(2)
      }
  }

  lazy val stdColHeaderStyle = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
        s.setFillPattern(CellStyle.SOLID_FOREGROUND)
        s.setAlignment(CellStyle.ALIGN_CENTER)
        s.setBorderBottom(CellStyle.BORDER_MEDIUM)
        s.setBottomBorderColor(IndexedColors.BLACK)
      }
  }
  lazy val stdColHeaderRotatedStyle = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
        s.setFillPattern(CellStyle.SOLID_FOREGROUND)
        s.setRotation(90)
        s.setIndention(1)
      }
  }

  lazy val stdDataCellString = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setAlignment(CellStyle.ALIGN_LEFT)
        s.setWrapText(true)
        s.setIndention(1)
        s.setVerticalAlignment(CellStyle.VERTICAL_TOP)

      }
  }
  lazy val stdDataCellInt = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setAlignment(CellStyle.ALIGN_RIGHT)
        s.setDataFormat(format("#"))
      }
  }
  lazy val stdDataCellDate = wb.getStyleBasedWith(stdStyle) {
    s =>
      {
        s.setAlignment(CellStyle.ALIGN_RIGHT)
        s.setDataFormat(stdDateFormat)
      }
  }
}
