package com.odenzo.utils.excel.examples

import java.io.File
import java.net.URL

import com.odenzo.archcatalogs.functional.BestProgram._
import com.odenzo.archcatalogs.functional.FunctionalTreeParser._
import com.odenzo.archcatalogs.functional.{ FunctionalTreeParser, FunctionalUnit }
import com.odenzo.utils.excel.{ StdExcelStyles, ExcelFns }
import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel.Workbook
import org.fancypoi.excel.FancyWorkbook

import scala.xml.NodeSeq._
import scala.xml.{ Node, XML }
import scalaz.Tree

/**
 * A little example or writing an Excel sheet.
 * See  that  was made to  inventory like sheets.
 * application I choose not to re-use stuff in FunctionalTreeParser to make it a bit easier
 * for you to play with alternative ways of representing.
 *
 */
object ExampleExcelOut extends App with LazyLogging {

  // Main area called on object construction (i.e. running)
  val url = new URL(args.headOption.getOrElse("../Business/MobileBanking.xml"))
  logger.info(s"XML To Parse $url")
  val xml = XML.load(url)

  /**
   * In case you don't want to work with XML directly this converts to a tree.
   * Unfortunately, the Tree datastructure is from ScalaZ and a little complicated.
   * Especially for you since you may want to collapse XML elements
   * @param xml
   * @return
   */
  def traverseToModel(xml: Node): Tree[Any] = {

    val root = parseUnit(xml)

    // This actually starts iterating finding the first "fa" element

    xml \ "fa" match {
      case Empty => Tree(root)
      case children => // Depth First non-tail recursive. Good enough.
        val kidTreeNodes = children.map(traverseToModel).toStream
        Tree.node(root, kidTreeNodes)
    }
  }

  /**
   * This does not navigate through the XML per se, given an XML node (usually an XML element)
   * it converts it to a more workable data structure.
   * Since things are immutable you may want to just work in XML all the time.
   * @param faElem
   * @return
   */
  def parse(faElem: Node): (FunctionalUnit, Option[FuncUsage]) = {
    val name = faElem \@ "name" // Get attribute

    val usageAttr = (faElem \ "@bu").text.split(',').toSeq.filter(_.length > 0)
    val comments = attributesValue(faElem.attribute("comment")) // Option attribute, None or Some(String)

    val usageElem = (faElem \ "usage").headOption // Returns a list of child name usage
    // We get the head of the list, or None, on assumption one usage elem per fa elem (direct child)
    logger.debug("Direct Child named usage" + usageElem)

    val usageModel = usageElem.map(parseUsageElem _)
    (FunctionalUnit(name, None, usageAttr, comments), usageModel) // return a tuple
  }

  def parseUsageElem(e: Node): FuncUsage = {
    FuncUsage(Seq.empty, Seq.empty) // Returns empty for no
  }
}

case class Used(bu: String, sourceOfFunds: Seq[String])

// We define a FuncUsage class to hold usage information. Whatever you want here really.
case class FuncUsage(used: Seq[Used], planned: Seq[String])

object ExcelTemplate extends LazyLogging with ExcelFns with StdExcelStyles {

  override val wb = FancyWorkbook.createXlsx

  val coverSheet = wb.sheet("Cover Page") // Creates if doesn't exist
  val dataSheet = wb.sheet("Data")

}
