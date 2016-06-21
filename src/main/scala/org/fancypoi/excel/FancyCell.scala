package org.fancypoi.excel

import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel._
import java.util.{Calendar, Date}
import FancyExcelUtils._
import org.fancypoi.Implicits._

import org.fancypoi.excel.FancyCellType._

// Not Sure exactly what I am going to do with this. Scala type system still a mystery

case class TypedFancyCell(c : FancyCell, typed : FancyCellType.FancyCellType)

class FancyCell(protected[fancypoi] val _cell : Cell) extends LazyLogging {
  lazy val workbook = _cell.getSheet.getWorkbook

  override def toString = "#" + _cell.getSheet.getSheetName + "!" + addr

  def addr : String = colIndexToAddr(_cell.getColumnIndex) + (_cell.getRowIndex + 1)

  /**
   *
   * @return The column address in alphabetic format for this cell
   */
  def colAddr : String = colIndexToAddr(_cell.getColumnIndex)

  /**
   *
   * @return One based row address per FancyPOI 1 based standard as opposed to POI 0 based  (address vs index)
   */
  def rowAddr : Int = _cell.getRowIndex + 1

  def row : FancyRow = _cell.getRow

  def value : String = _cell.getStringCellValue

  def stringValue : String = _cell.getStringCellValue

  def numericValue : Double = _cell.getNumericCellValue

  def richTextValue : RichTextString = _cell.getRichStringCellValue

  def dateValue : Date = _cell.getDateCellValue

  def booleanvalue : Boolean = _cell.getBooleanCellValue

  def value(value : String) = {
    _cell.setCellValue(value)
    this
  }

  def value(value : Double) = {
    _cell.setCellValue(value)
    this
  }

  def value(value : RichTextString) = {
    _cell.setCellValue(value)
    this
  }

  def value(value : Calendar) = {
    _cell.setCellValue(value)
    this
  }

  def value(value : Date) = {
    _cell.setCellValue(value)
    this
  }

  def value(value : Boolean) = {
    _cell.setCellValue(value)
    this
  }

  def formula : String = {
    _cell.getCellFormula
  }

  def formula(formula : String) = {
    _cell.setCellFormula(formula)
    this
  }

  /**
   * Sets the cell to a Hyperlink (e.g. email address or http url
   * @param linkType
   * @param address
   * @return
   */
  def hyperlink(linkType : Int, address : String) = {
    val link = workbook.getCreationHelper.createHyperlink(linkType)
    link.setAddress(address)
    _cell.setHyperlink(link)
    this
  }

  def hyperlink : Hyperlink = _cell.getHyperlink

  def style = _cell.getCellStyle

  /**
   * Duplicate of styleFont?
   * @return
   */
  def font = workbook.getFontAt(_cell.getCellStyle.getFontIndex)

  /**
   * Index of the Font applied to this cell via Style. May be applied to multiple cells.
 *
   * @return
   */
  def styleFont: Font = workbook.getFontAt(style.getFontIndex)

  /**
   * フォントを更新します。
   * 変更した設定値以外は、既存の値を引き継ぎます。
   */
  def updateFont(block : Font ⇒ Unit) = {
    val updatedFont = workbook.getFontBasedWith(workbook.getFontAt(_cell.getCellStyle.getFontIndex))(block)
    updateStyle(_.setFont(updatedFont))
    this
  }

  /**
   * フォントを新規に設定します。
   * 設定していない値には、デフォルトの値が設定されます。
   */
  def replaceFont(block : Font ⇒ Unit) = {
    val newFont = workbook.getFontWith(block)
    updateStyle(_.setFont(newFont))
    this
  }

  /**
   * セルスタイルを置き換えます。
   */
  def replaceStyle(styleObj : CellStyle) = {
    _cell.setCellStyle(styleObj)
    this
  }

  /**
   * セルスタイルを更新します。
   * 変更した設定値以外は、既存の値を引き継ぎます。
   */
  def updateStyle(block : CellStyle ⇒ Unit) = {
    val updatedStyle = workbook.getStyleBasedWith(_cell.getCellStyle)(block)
    _cell.setCellStyle(updatedStyle)
    this
  }

  /**
   * セルスタイルを新規に設定します。
   * 設定していない値には、デフォルトの値が設定されます。
   */
  def replaceStyle(block : CellStyle ⇒ Unit) = {
    val style = workbook.getStyle(block)
    _cell.setCellStyle(style)
    this
  }

  def cellType : FancyCellType.Value = {
    FancyCellType(_cell.getCellType)
  }

  /**
   *
   * @return The string repr of the data format applied to the current cell style.
   */
  def dataFormatString = _cell.getCellStyle.getDataFormatString

  /**
   *
   * @return The index of the data format applied to the current cell style.
   */
  def dataFormat : Int = _cell.getCellStyle.getDataFormat

  /**
   *
   *
   * @return The native cell type, for ERROR and BLANK cells None is returned. For formulata cells the (cached) result
   *         of forumla .
   */
  def nativeValue : Option[Any] = {
    import FancyCellType._

    cellType match {
      case ERROR_CELL                                          ⇒ None
      case BLANK_CELL                                          ⇒ None
      case BOOLEAN_CELL                                        ⇒ Some(_cell.booleanvalue)
      case NUMERIC_CELL if DateUtil.isCellDateFormatted(_cell) ⇒ Some(DateUtil.getJavaDate(_cell.numericValue))
      case NUMERIC_CELL                                        ⇒ Some(_cell.numericValue)
      case STRING_CELL                                         ⇒ Some(_cell.stringValue)
      case FORMULA_CELL                                        ⇒ formulaValue
      case _                                                   ⇒ logger.error("Uknown Cell Type " + cellType); None
    }
  }

  /**
   * This tries to apply the data formatting to get intended cell type.
   * So, it returns the displayed String always. This won't evaluate formulas because no FormulaEvaluator passed in.
   * Maybe later.
   * @return
   */
  def formattedValue : Option[String] = {
    val str = FancyDataFormatter.format(this)
    if (str.isEmpty) None else Some(str)
  }

  /**
   * Get the cached value of a formula. Note the small subset of types. But Dates can have formula,
   * returns at Numeric Value. Errors are returns as None. Error if non formula cell passed in.
   * This is typically changes from nativeValue so far and not called directly from clients.
   * @return
   * @author stevef
   */
  def formulaValue = {
    FancyCellType(_cell.getCachedFormulaResultType) match {
      case BOOLEAN_CELL ⇒ Some(_cell.booleanvalue)
      case NUMERIC_CELL ⇒ Some(_cell.numericValue)
      case STRING_CELL  ⇒ Some(_cell.stringValue)
      case ERROR_CELL   ⇒ None
    }
  }

}
