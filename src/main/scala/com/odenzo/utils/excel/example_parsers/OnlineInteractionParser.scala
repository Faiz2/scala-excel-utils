package com.odenzo.utils.excel.example_parsers

import com.odenzo.utils.excel.GenericExcelParser
import org.fancypoi.excel.FancyRow
import org.fancypoi.excel.FancyWorkbook

/**
 *
 * Created on 8/07/13
 *
 * @author Steve Franks (e1040775)
 */
class OnlineInteractionParser(wb : FancyWorkbook) extends GenericExcelParser(wb) {

  // Sometimes the first column is empty.... so check the second column to determine empty row.
  override val rowFilter = (row : FancyRow) ⇒ row.addr != "1" && cellNotEmpty(row.cellAt(2))

  override val aliasColumnNames = Map(
    "Business Function (FIS)" → "FIS_Function",
    "Business Function (Bank)" → "PNB_Function",
    "Details/Comments" → "Description",
    "Remark" → "Remark"
  )
}
