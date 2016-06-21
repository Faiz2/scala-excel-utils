package com.odenzo.utils.excel

import com.typesafe.scalalogging.StrictLogging
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook

/**
 * This top level generic excel parser that is designed to parse one particular sheet
 * in an Excel workbook.
 * <p>
 * THe basic approach is to filter out non-relevant data rows.
 * For each data row, information is pulled out by column and stored in Map[key,colValue] .
 * The pulling is driven from Map[ColumnID,(ColumnConsumer, Key)] (ColumnID is currently Column Index,
 * could be Column Addr I guess.
 * The key can be setup by either using Column labels (run through an optional Aliaser)
 *
 * @author Steve Franks
 */
class GenericMatrixExcelParser(wb : Workbook) extends GenericExcelParserV2(wb) with StrictLogging {



  logger.info(s"GenericExcelParser for workbook: $wb with sheet $sheet")

  /**
   *
   * @param r  The current row being parsed
   * @param rowMap  Int is the column index, String is the property to store parsed value in results
   * @return   Map[property,Value] where property is from rowMap and Value is from CellConsumer
   *
   *
   *
   */
  override protected def rowParser(r : Row, rowMap : Map[Int, String]) : Map[String, Any] = {
    val res = rowMap.flatMap {
      case (cellIndx, prop) ⇒
        val extractFn = customExtractors.getOrElse(prop, defaultDataCellConsumer)
        val v = extractFn(r.getCell(cellIndx))
        if (v.isDefined) Some(prop → v.get)
        else None
    }

    res + ("_rowAddr" → (r.getRowNum+1))
  }

}
