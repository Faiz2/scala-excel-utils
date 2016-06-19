package com.odenzo.utils.excel

import com.typesafe.scalalogging.LazyLogging

import org.fancypoi.Implicits._
import org.fancypoi.excel.{ FancyCell, FancySheet, FancyWorkbook }

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
@Deprecated()
class GenericExcelParser(val wb: FancyWorkbook) extends LazyLogging with ExcelFns {

  import org.fancypoi.excel.FancyRow

  /**
   * You should subclass class this parser to specify what sheet to parse
   */
  val sheet: FancySheet = wb.sheetAt(0)

  logger.info(s"GenericExcelParser for workbook: $wb with sheet $sheet")

  /**
   * Rows that do not pass this filter will not be processed.
   * Header row should be filtered in this filter.
   * The default just checks that rowAddr is now 1 and the first cell (Col A) is not empty.
   * This suites most common data that has Header Row at Row 1 and First Cell is Label for each data row.
   */
  protected val rowFilter = (row: FancyRow) ⇒ row.addr != "1" && rowNotEmpty(row)

  /**
   * Columns that do not pass this filter will be excluded from parsed results.
   * This is actually applied to the Col Header Row to build the Row Extraction Map
   */
  protected val colFilter = (cell: FancyCell) ⇒ cellNotEmpty(cell)

  // In the end each Cell Parser needs to consume a cell.
  // Users can partially applies this to the end of the universe and chain calls.
  type CellConsumer = (FancyCell) ⇒ Option[Any]

  val defaultDataCellConsumer: CellConsumer = _.nativeValue

  /**
   * Canonical sheet has canonical column names of course.
   * Subclasses can use this to map a Column Header to Canonical property Name
   * If not present, canonical name is used, this may cause errors is standard properties don't have column name
   * or aliased column names.
   * Sigh... this maps column name found in excel to a property name. If column name not found the identity mapping
   * is used.
   */
  val aliasColumnNames: Map[String, String] = Map()

  /**
   * The String key is the final (post aliasing) column property and CellConsumer is function to use to parse
   * the data rows at the given column corresponding to the property. If none, then defaultDataCellConsumer is used.
   * Note that CellConsumers must be able to deal with null and Blank cells by (usually) returning None.
   */
  val customExtractors: Map[String, CellConsumer] = Map()

  /**
   *
   * @param r  The current row being parsed
   * @param rowMap  Int is the column index, String is the property to store parsed value in results
   * @return   Map[property,Value] where property is from rowMap and Value is from CellConsumer
   *
   *
   *
   */
  protected def rowParser(r: FancyRow, rowMap: Map[Int, String]): Map[String, Any] = {
    val res = rowMap.flatMap {
      case (cellIndx, prop) ⇒
        val extractFn = customExtractors.getOrElse(prop, defaultDataCellConsumer)
        val v = extractFn(r.cellAt(cellIndx))
        if (v.isDefined) Some(prop -> v.get)
        else None

    }
    res + ("_rowAddr" -> r.addr) // Add row address (index + 1) for debugging porpoises
  }

  /**
   * Parse all column headers until an empty cell is found.
   * Return as Map[colIndex, propertyName] where propertName is the value of possibly aliased headerCell.
   * All headerCell contents that are in ignoredColumns are discarded prior to aliasing headerCellValue to property
   * name.
   * Blank column headers are by default (a) Not in Map, or (b) Mapped by Column Address then aliased?
   * <strong>This is generic.</strong>
   */
  protected def buildRowMapFromColumnHeaders(colHeaderRow: Int = 0): Map[Int, String] = {

    // Tuples (colIndex, colHeaderString (or addr is col header is empty))
    val rawColumns = sheet.rowAt(colHeaderRow).cells.filter(colFilter).map(c ⇒
      (c.getColumnIndex, c.formattedValue.getOrElse(c.addr)))

    logger.info("Raw Columns:" + rawColumns.mkString(","))
    rawColumns.toMap.mapValues(h ⇒ aliasColumnNames.getOrElse(h, h))
  }

  /**
   * Returns list of Map of row properties now, so we loose the Row # information.
   * Maybe should always insert in _rowAddr property?
   * @param rowColToPropMap
   * @return
   */
  protected def parseRows(rowColToPropMap: Map[Int, String]) = {
    val rows = sheet.rows.filter(rowFilter)
    logger.info(s"Parsing ${rows.size} rows starting at ${rows.head.addr} Index ${rows.head.index}")
    rows.map(rowParser(_, rowColToPropMap))
  }

  /**
   * Main execution function is pre-packaged form.
   * @return Each list item represents a row, as a Map from property to value. If no value for a property then not in
   *         map. Recall a property is actually just a name/token for a column.
   */
  def parse: List[Map[String, Any]] = {
    logger.info("Parse Beginging (Workbook loaded already... Parsing Sheet: " + sheet)
    val mapper = buildRowMapFromColumnHeaders()
    parseRows(mapper)
  }

}