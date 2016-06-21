package com.odenzo.utils.excel.example_parsers

import com.odenzo.utils.excel.GenericMatrixExcelParser
import com.typesafe.scalalogging.LazyLogging
import org.fancypoi.excel.FancySheet
import org.fancypoi.excel.FancyWorkbook

/**
  * Demonsrates the simple user of mapping column names in header cells to key names
  **/
class AliasingColumnsParser(wb: FancyWorkbook) extends GenericMatrixExcelParser(wb) with LazyLogging {

  override val sheet: FancySheet = wb.sheetAt(3) // Title is "Application Inventory"

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
