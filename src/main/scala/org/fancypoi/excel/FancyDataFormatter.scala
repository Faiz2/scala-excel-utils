package org.fancypoi.excel

import org.apache.poi.ss.usermodel.DataFormatter
import org.fancypoi.Implicits._

object FancyDataFormatter {
  val formatter = new DataFormatter()

  def format(cell: FancyCell) = formatter.formatCellValue(cell)

}
