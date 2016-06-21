package com.odenzo.utils.excel.example_parsers

import com.odenzo.utils.excel.GenericExcelParserV2
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook

/**
 *
 * Created on 8/07/13
 *
 * @author Steve Franks (e1040775)
 */
class OnlineInteractionParser(wb : Workbook) extends GenericExcelParserV2(wb) {

  // Sometimes the first column is empty.... so check the second column to determine empty row.
  override val rowFilter = (row : Row) ⇒ row.getRowNum != 0 && cellNotEmpty(row.getCell(2))

  override val aliasColumnNames = Map(
    "Business Function (FIS)" → "FIS_Function",
    "Business Function (Bank)" → "PNB_Function",
    "Details/Comments" → "Description",
    "Remark" → "Remark"
  )
}
