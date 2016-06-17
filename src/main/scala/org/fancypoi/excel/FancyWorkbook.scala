package org.fancypoi.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import FancyExcelUtils._
import org.apache.poi.ss.usermodel._
import org.fancypoi.Implicits._

/**
 * Updated by SteveF to return Fancy objects.
 * User: ishiiyoshinori
 * Date: 11/05/04
 */

object FancyWorkbook {

  def createXls: FancyWorkbook = new HSSFWorkbook

  def createXlsx: FancyWorkbook = new XSSFWorkbook

  def createFromFile(file: File): FancyWorkbook = {
    val fis = new FileInputStream(file)
    val w = WorkbookFactory.create(fis)
    fis.close()
    w
  }

  def createFromInputStream(is: InputStream): FancyWorkbook = {
    val w = WorkbookFactory.create(is)
    is.close()
    w
  }

}

class FancyWorkbook(protected[fancypoi] val workbook: Workbook) {
  override def toString = "Workbook"

  protected[fancypoi] val defaultFont = new FancyFont

  protected[fancypoi] val defaultStyle = {
    val style = new FancyCellStyle
    style.setFont(defaultFont)
    style
  }

  def dataFormat: DataFormat = workbook.createDataFormat() // POI has underlying cache, no need to duplicate

  protected[fancypoi] val tmpFont = new FancyFont

  protected[fancypoi] val tmpStyle = new FancyCellStyle

  def getFontAt(index: Short) = index == FancyFont.DEFAULT_FONT_INDEX match {
    case true => defaultFont
    case false => workbook.getFontAt(index)
  }

  def getStyleAt(index: Short) = index == FancyCellStyle.DEFAULT_CELL_STYLE_INDEX match {
    case true => defaultStyle
    case false => workbook.getCellStyleAt(index)
  }

  /**
   * blockで設定したフォントを取得します。
   * 既に同じ値を持つフォントがある場合は、それを返し新しいフォントは生成しません。
   */
  def getFontWith(block: Font => Unit): Font = getFontBasedWith(defaultFont)(block)

  /**
   * ベースとなるフォントを指定し、blockで設定したフォントを取得します。
   * 既に同じ値を持つフォントがある場合は、それを返し新しいフォントは生成しません。
   * This doesn't seem threadsafe for starters.
   */
  def getFontBasedWith(base: Font)(block: Font => Unit): Font = {
    copyFont(base, tmpFont) // デフォルトフォントを一時フォントにコピー
    block(tmpFont) // 一時フォントを設定
    searchFont(this, tmpFont) match {
      case Some(font) => font // フォントがすでにある場合はそれを返す
      case None =>
        val newFont = workbook.createFont
        copyFont(tmpFont, newFont)
        newFont
    }
  }

  /**
   * A create with easier definition syntax, but which will create possible duplicates.
   * (Which we actually want sometimes!)
   * @param base
   * @param block
   * @return
   */
  def createStyleBasedWith(base: CellStyle)(block: CellStyle => Unit) = {
    val style = workbook.createCellStyle()
    copyStyleWithoutFont(base, style)
    style.setFont(workbook.getFontAt(base.getFontIndex))
    // Blah, this still won't help, since closure done at definition not runtime
    // Have to override a FancyCellStyle class anonmyously and overide a function to get in scope.
    block(style)

  }

  /**
   * blockで設定したセルスタイルを取得します。
   * 既に同じ値を持つセルスタイルがある場合は、それを返し新しいセルスタイルは生成しません。
   */
  def getStyle(block: CellStyle => Unit): CellStyle = getStyleBasedWith(defaultStyle)(block)

  /**
   * ベースとなるセルスタイルを指定し、blockで設定したスタイルを取得します。
   * 既に同じ値を持つセルスタイルがある場合は、それを返し新しいセルスタイルは生成しません。
   *
   */
  def getStyleBasedWith(base: CellStyle)(block: CellStyle => Unit) = {

    // This will make a temporary style and put in tmpStyle, shared not MT safe?
    // Applies the styleing in block
    // Searches to see if an existing style is in the worksheet, if is, return that without creating a new one.
    // If not, create and return the new style
    // FIXME: unsure why a local tmpStyle isn't used to ensure no race conditions
    copyStyleWithoutFont(base, tmpStyle)
    copyFont(getFontAt(base.getFontIndex), tmpFont)
    tmpStyle.setFont(tmpFont)

    // - Set the style based on the block of code passed in.
    //
    block(tmpStyle)

    // ワークブックから取得したいセルスタイルを検索し、ない場合は生成する。
    searchStyle(this, tmpStyle) match {
      case Some(style) => style
      case None =>
        val newStyle = workbook.createCellStyle
        copyStyleWithoutFont(tmpStyle, newStyle)
        val font = tmpStyle.getFontIndex == tmpFont.getIndex match {
          case true =>
            val f = workbook.createFont
            copyFont(tmpFont, f)
            f
          case false => workbook.getFontAt(tmpStyle.getFontIndex)
        }
        newStyle.setFont(font)
        newStyle
    }
  }

  /**
   * シートを名前で検索します
   */
  def sheet(name: String): FancySheet = Option(workbook.getSheet(name)) match {
    case None => workbook.createSheet(name)
    case Some(sheet) => sheet
  }

  def sheet_?(name: String): Option[FancySheet] = Option(workbook.getSheet(name))

  /**
   * シートをインデックスで検索し、blockを適用します。
   */
  def sheetAt(index: Int): FancySheet = workbook.getSheetAt(index)

  def sheetAt_?(index: Int): Option[FancySheet] = workbook.getNumberOfSheets - 1 < index match {
    case true => None
    case false => Some(workbook.getSheetAt(index))
  }

  /**
   * シートをリストで返します。
   */
  def sheets = (0 to workbook.getNumberOfSheets - 1).map(sheetAt).toList

  /**
   * ワークブックをファイルに書き出します。
   */
  def write(file: File) {
    val fos = new FileOutputStream(file)
    val bos = new BufferedOutputStream(fos)
    workbook.write(bos)
    bos.close()
  }
}
