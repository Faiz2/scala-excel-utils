package com.odenzo.utils.excel.examples

import org.fancypoi.excel.FancyWorkbook

/**
 *
 * Created on 1/03/13
 * @author Steve Franks (e1040775)
 * @version $Id$
 */
class AlliedApplicationInventoryParser(wb: FancyWorkbook) extends ApplicationInventoryParser(wb, "AlliedApps") {

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
