package com.odenzo.utils.excel.example_parsers

import com.odenzo.utils.excel.ExcelFns
import com.odenzo.utils.excel.StdExcelStyles
import com.typesafe.scalalogging.LazyLogging
import org.fancypoi.excel.FancyWorkbook

object ExampleExcelOut extends LazyLogging with ExcelFns with StdExcelStyles {

  override val wb = FancyWorkbook.createXlsx

  val coverSheet = wb.sheet("Cover Page") // Creates if doesn't exist
  val dataSheet = wb.sheet("Data")

}
