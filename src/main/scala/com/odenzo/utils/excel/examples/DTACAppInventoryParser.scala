package com.odenzo.utils.excel.examples

import com.odenzo.utils.excel.GenericMatrixExcelParser
import com.typesafe.scalalogging.LazyLogging
import org.fancypoi.excel.FancySheet
import org.fancypoi.excel.FancyWorkbook

/**
 * Created by stevef on 16/03/15.
 */
class DTACAppInventoryParser(wb: FancyWorkbook) extends GenericMatrixExcelParser(wb) with LazyLogging {

  override val sheet: FancySheet = wb.sheetAt(3) // Title is "Application Inventory"

  /**
   * This only has to be done with the required standard properties which have different column names
   */
  override val aliasColumnNames = Map[String, String](
    "App ID" -> "key",
    "Abbreviation" -> "shortName",
    "Name" -> "name",
    "Description" -> "description"
  )

}
