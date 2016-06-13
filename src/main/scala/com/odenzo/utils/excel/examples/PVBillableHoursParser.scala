package com.odenzo.utils.excel.examples

import com.odenzo.utils.excel.GenericExcelParser
import com.typesafe.scalalogging.LazyLogging
import org.fancypoi.excel.FancyRow
import org.fancypoi.excel.FancyWorkbook

/**
 *
 * Created on 18/06/13
 * @author Steve Franks (e1040775)
 * @version $Id$
 */
class PVBillableHoursParser(wb: FancyWorkbook) extends GenericExcelParser(wb) with LazyLogging {

  logger.info("Billable Hours Constructing...")
  override val sheet = {
    val res = wb.sheetAt(6) // Zero based but hidden sheet normally
    logger.info("Got Sheet: " + res)
    res
  }

  override val rowFilter = (r: FancyRow) => r.addr != "1" && filterOnMyBU(r)

  def filterOnMyBU(r: FancyRow): Boolean = {

    val homeBu = r.cell("A").formattedValue

    //	logger.debug(s"Filter Row on my BU $homeBu  ${homeBu.getClass}  Row: $r")
    homeBu match {
      case Some("810354003") => true
      case _ => false
    }
  }

}
