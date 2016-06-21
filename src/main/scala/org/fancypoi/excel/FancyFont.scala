package org.fancypoi.excel

import org.apache.poi.ss.usermodel.Font
import org.fancypoi.False

object FancyFont {
  val DEFAULT_FONT_INDEX: Short = -1

  def apply(fontName : String = "Arial", heightInPoints : Short = 12, bold : Boolean = false, italic : Boolean = false) = {
    val f = new FancyFont()
    f.setFontName(fontName)
    f.setFontHeightInPoints(heightInPoints)
    f.setItalic(italic)
    if (bold) f.setBoldweight(800)
    f
  }

}

/**
 * Mutable class that implements the Java inteface trait
 */
class FancyFont extends Font {

  /**
    * Once added to an Excel Sheet the Font has an index?
    * @return
    */
  def getIndex : Short = FancyFont.DEFAULT_FONT_INDEX

  private var _name : String = "Arial"
  private var _fontHeight : Short = 200
  private var _italic : Boolean = false
  private var _strikeout : Boolean = false
  private var _color : Short = 0x7fff.toShort
  private var _offset : Short = 0
  private var _underline : Byte = Font.U_NONE
  private var _charset : Int = 0
  private var _boldweight : Short = 400
  private var _bold : Boolean = false

  def setFontName(name : String) { _name = name }

  def getFontName : String = _name

  def setFontHeight(height : Short) { _fontHeight = height }

  def setFontHeightInPoints(height : Short) {
    _fontHeight = (height * 20).toShort
  }

  def getBold : Boolean = _bold

  def setBold(b : Boolean) = _bold = b

  def getFontHeight : Short = _fontHeight

  def getFontHeightInPoints : Short = (_fontHeight * 20).toShort

  def setItalic(italic : Boolean) : Unit = _italic = italic

  def getItalic : Boolean = _italic

  def setStrikeout(strikeout : Boolean) : Unit = _strikeout = strikeout

  def getStrikeout : Boolean = _strikeout

  def setColor(color : Short) : Unit = _color = color

  def getColor : Short = _color

  def setTypeOffset(offset : Short) : Unit = _offset = offset

  def getTypeOffset : Short = _offset

  def setUnderline(underline : Byte) : Unit = _underline = underline

  def getUnderline : Byte = _underline

  def setCharSet(charset : Byte) : Unit = _charset = charset.toInt

  def setCharSet(charset : Int) : Unit = _charset = charset

  def getCharSet : Int = _charset

  def setBoldweight(boldweight : Short) : Unit = _boldweight = boldweight

  def getBoldweight : Short = _boldweight
}
