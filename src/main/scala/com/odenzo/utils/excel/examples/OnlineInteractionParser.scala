package com.odenzo.utils.excel.examples

import com.odenzo.utils.excel.GenericExcelParser
import org.fancypoi.excel.FancyRow
import org.fancypoi.excel.FancyWorkbook

object OnlineInteractionParser {
  def formatRecord(rec: Map[String, Any]): String = {
    s"""
					 |  From   : \t ${rec("Source")} \t Target: ${rec("Target")}
					 |  FIS Fn : \t ${rec.get("FIS_Function").getOrElse("--")}
					 |  nPNB   : \t ${rec.getOrElse("PNB_Function", "--")}
					 |  Descr  : \t ${rec.getOrElse("Description", "--")}
					 |  Remarks: \t ${rec.getOrElse("Remarks", "")}
					 |  Row    : \t ${rec("_rowAddr")}
					     """.stripMargin
  }
}

/**
 *
 * Created on 8/07/13
 *
 * @author Steve Franks (e1040775)
 * @version $Id$
 */
class OnlineInteractionParser(wb: FancyWorkbook) extends GenericExcelParser(wb) {

  // Sometimes the first column is empty.... so check the second column to determine empty row.
  override val rowFilter = (row: FancyRow) => row.addr != "1" && cellNotEmpty(row.cellAt(2))

  override val aliasColumnNames = Map(
    "Business Function (FIS)" -> "FIS_Function",
    "Business Function (Bank)" -> "PNB_Function",
    "Details/Comments" -> "Description",
    "Remark" -> "Remark"
  )
}
