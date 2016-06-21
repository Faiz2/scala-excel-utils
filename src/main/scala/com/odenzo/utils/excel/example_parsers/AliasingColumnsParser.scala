package com.odenzo.utils.excel.example_parsers

import com.odenzo.utils.excel.GenericMatrixExcelParser
import com.typesafe.scalalogging.StrictLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

/**
  * Demonsrates the simple user of mapping column names in header cells to key names
  **/
class AliasingColumnsParser(wb: Workbook) extends GenericMatrixExcelParser(wb) with StrictLogging {

  override val sheet: Sheet = wb.getSheetAt(3) // Title is "Application Inventory"

  /**
    * This only has to be done with the required standard properties which have different column names
    * The other columns will still be pulled out. :-(
    **/
  override val aliasColumnNames = Map[String, String](
    "App ID" → "key",
    "Abbreviation" → "shortName",
    "Name" → "name",
    "Description" → "description"
  )

}
