package com.odenzo.utils.excel

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors

/**
 * Some common styles for writing out Excel sheets. These are not true utilities but time savers for me.
 *
 */
trait StdExcelStyles {

  val wb: Workbook // Abstract, so must be in the class we are mixing into, no need for implicits (?)

  import org.fancypoi.Implicits._

  // wb is not initialized straight away before here, so can make lazy or move to earliy init I guess.
  lazy val stdFont = wb.getFontWith(_.setFontHeightInPoints(14))
  lazy val stdHeaderFont = wb.getFontBasedWith(stdFont)(_.setBoldweight(600))
  lazy val dataFormat = wb.createDataFormat()
  lazy val stdDateFormat = format("yyyy-mm-dd")

  lazy val stdStyle = wb.getStyle { s ⇒
    s.setAlignment(CellStyle.ALIGN_CENTER)
    s.setFont(stdFont)
  }

  lazy val stdRowHeaderStyle = wb.getStyleBasedWith(stdStyle) { cellStyle ⇒
    cellStyle.setAlignment(CellStyle.ALIGN_LEFT)
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    cellStyle.setIndention(2)

  }
  lazy val stdColHeaderStyle = wb.getStyleBasedWith(stdStyle) { cellStyle ⇒
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER)
    cellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM)
    cellStyle.setBottomBorderColor(IndexedColors.BLACK)
  }

  lazy val stdColHeaderRotatedStyle = wb.getStyleBasedWith(stdStyle) { cellStyle ⇒
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)
    cellStyle.setRotation(90)
    cellStyle.setIndention(1)

  }
  lazy val stdDataCellString = wb.getStyleBasedWith(stdStyle) { cellStyle ⇒
    cellStyle.setAlignment(CellStyle.ALIGN_LEFT)
    cellStyle.setWrapText(true)
    cellStyle.setIndention(1)
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP)
  }

  lazy val stdDataCellInt = wb.getStyleBasedWith(stdStyle) {
    s ⇒
      {
        s.setAlignment(CellStyle.ALIGN_RIGHT)
        s.setDataFormat(format("#"))
      }
  }
  lazy val stdDataCellDate = wb.getStyleBasedWith(stdStyle) {
    s ⇒
      {
        s.setAlignment(CellStyle.ALIGN_RIGHT)
        s.setDataFormat(stdDateFormat)
      }
  }

  def format(format: String) = dataFormat.getFormat(format)
}
