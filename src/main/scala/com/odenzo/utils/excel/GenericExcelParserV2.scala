package com.odenzo.utils.excel

import com.typesafe.scalalogging.StrictLogging
import org.apache.poi.ss.usermodel._

/**
  * This is really the heart of it.
  * But, its really a Functor applied to a Row, and extracts things to return an Object Value
  */
abstract class ExcelRowMapping extends StrictLogging {

  def apply()

}

trait GenericParserConfig extends ExcelFns {
  val wb: Workbook


  /**
    * Used to define the row bounding extent of possible rows to parse.
    * Not that this may differ from isEmptyRow
    *
    * @param r
    *
    * @return
    */
  def isLastContentRow(r: Row): Boolean = isEmptyRow(r)


  /**
    * Compute the last column in this row that has usable content.
    * Used to compute bounding extent of columns to parse, either min of these or max usually.
    *
    * @param r
    *
    * @return
    */
  def lastContentCol(r: Row): Int = {
    val last = r.getLastCellNum
    val physicalLast = r.getPhysicalNumberOfCells
    last // FIXME: Bad example default lastContentCol
  }


  /**
    * Used to filter rows to parse
    *
    * @param r
    *
    * @return
    */
  def isEmptyRow(r: Row): Boolean = {
    if (r == null) return true
    val firstCellIndx = r.getFirstCellNum
    !(firstCellIndx == 0 && cellNotEmpty(r.getCell(firstCellIndx)))
  }
}

/**
  * An attempt at easy parsing without FancyPOI
  **/
class GenericExcelParserV2(val wb: Workbook) extends StrictLogging with ExcelFns {

  import scala.collection.JavaConverters._

  // In the end each Cell Parser needs to consume a cell.
  // Users can partially applies this to the end of the universe and chain calls.
  type CellConsumer = (Cell) ⇒ Option[Any]

  /**
    *
    */
  val defaultDataCellConsumer: CellConsumer        = cellContentSmart
  /**
    * Normalize column names found in the excel sheet by mapping strings.
    * TODO: Nice to have an option of Mapping Column Label ("A" -> "Agent ID")
    * Column names are used as a key in the results.
    * If column name not found in map the raw (trimmed) column name is used from header cell for column
    *
    */
  val aliasColumnNames       : Map[String, String] = Map()

  /**
    * The String key is the final (post aliasing) column property and CellConsumer is function to use to parse
    * the data rows at the given column corresponding to the property. If none, then defaultDataCellConsumer is used.
    * Note that CellConsumers must be able to deal with null and Blank cells by (usually) returning None.
    */
  val customExtractors: Map[String, CellConsumer] = Map()
  /**
    * You should subclass class this parser to specify what sheet to parse
    */
  protected val sheet    : Sheet            = wb.getSheetAt(0)
  /**
    * Rows that do not pass this filter will not be processed.
    * Header row should be filtered in this filter.
    * The default just checks that rowAddr is now 1 and the first cell (Col A) is not empty.
    * This suites most common data that has Header Row at Row 1 and First Cell is Label for each data row.
    */
  protected val rowFilter: (Row) ⇒ Boolean  = {
    (row: Row) ⇒ row.getRowNum != 0 && rowNotEmpty(row)
  }
  /**
    * Columns that do not pass this filter will be excluded from parsed results.
    * This is actually applied to the Col Header Row to build the Row Extraction Map
    */
  protected val colFilter: (Cell) ⇒ Boolean = cellNotEmpty(_)

  /**
    * Main execution function is pre-packaged form.
    *
    * @return Each list item represents a row, as a Map from property to value. If no value for a property then not in
    *         map. Recall a property is actually just a name/token for a column.
    */
  def parse: Seq[Map[String, Any]] = {
    logger.info("Parse Beginging (Workbook loaded already... Parsing Sheet: " + sheet)
    val mapper = buildRowMapFromColumnHeaders()
    parseRows(mapper).toSeq
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

    case class ColumnMapEntry(indx:Int, label:String)

    // Tuples (colIndex, colHeaderString (or addr is col header is empty))
    val rawHeaderCells = filteredCells(sheet.getRow(colHeaderRow))(colFilter)

    // Mapping of column index to canonical label to use for the column
    val sheetMap = rawHeaderCells.map{ cell ⇒
      val label:String = cellContentOptString(cell).getOrElse(cell.getAddress.formatAsString)
      ColumnMapEntry(cell.getColumnIndex, label)
    }

    val aliasedMap = sheetMap.map { entry ⇒
        aliasColumnNames.get(entry.label).fold(entry) { newLabel ⇒
          entry.copy(label = newLabel)
        }
    }

    // Hack to convert back to original hack format.
    aliasedMap.map(entry ⇒ (entry.indx, entry.label)).toMap
  }

  /**
    * Returns list of Map of row properties now, so we loose the Row # information.
    * Maybe should always insert in _rowAddr property?
    *
    * @param rowColToPropMap This is extraction based parsing, will take key as column and put value in
    *
    * @return
    */
  protected def parseRows(rowColToPropMap: Map[Int, String]) = {
    val rows = sheet.rowIterator().asScala
               .filter(rowFilter)
               .map(row ⇒ rowParser(row, rowColToPropMap))
    rows
  }

  /**
    *
    * @param r
    * The current row being parsed, just for debugging/traceing
    * @param rowMap
    * Int is the column index, String is the property to store parsed value in results.
    * This is after column filtering and extraction has been done.
    * Key is the column index, Value is the property to store the resultant value
    *
    * @return Map[property,Value] where property is from rowMap and Value is from CellConsumer
    *
    *
    *
    */
  protected def rowParser(r: Row, rowMap: Map[Int, String]): Map[String, Any] = {
    val res = rowMap.flatMap {
      case (cellIndx, prop) ⇒
        val extractFn = customExtractors.getOrElse(prop, defaultDataCellConsumer)
        val v = extractFn(r.getCell(cellIndx))
        if (v.isDefined) Some(prop → v.get)
        else None

    }
    res + ("_rowAddr" → (r.getRowNum+1).toString)
  }
}
