package org.fancypoi

import excel.FancyExcelUtils.AddrRangeStart
import excel.{ FancyWorkbook, FancySheet, FancyRow, FancyCell }
import org.apache.poi.ss.usermodel._
import scala.language.implicitConversions

/**
 *
 * User: ishiiyoshinori
 * Date: 11/05/04
 */

object Implicits {

  implicit def workbook2fancy(w: Workbook): FancyWorkbook = new FancyWorkbook(w)

  implicit def sheet2fancy(s: Sheet): FancySheet = new FancySheet(s)

  implicit def row2fancy(r: Row): FancyRow = new FancyRow(r)

  implicit def cell2fancy(c: Cell): FancyCell = new FancyCell(c)

  implicit def workbook2plain(w: FancyWorkbook): Workbook = w.workbook

  implicit def sheet2plain(s: FancySheet): Sheet = s._sheet

  implicit def row2plain(r: FancyRow): Row = r._row

  implicit def cell2plain(c: FancyCell): Cell = c._cell

  implicit def indexedColors2Int(indexedColor: IndexedColors): Short = indexedColor.getIndex

  implicit def str2Addr(addr: String): AddrRangeStart = new AddrRangeStart(addr)

}
