package com.odenzo.utils.excel

import org.fancypoi.excel.FancyCell
import scala.reflect.runtime.universe._

/**
 * Want to make a cell parsing DSL as end goal, with some default verbs and let use make custom verbs
 */
object CellParsers {
  // In the end each Cell Parser needs to consume a cell.
  // Users can partially applies this to the end of the universe and chain calls.
  // Would actually like to make this a parameterized type
  type CellParser = (FancyCell) ⇒ Option[Any]
}

trait CellParser[T] {

  /**
   * Think about T, Option[T], Either[Error,T] Either[Error,Option[T]] appraoches
   *
   * @param c
   * @return
   */
  def extract(c: FancyCell): Option[T]
}

trait FnCellParser {
  def extract[T: TypeTag](c: FancyCell): Option[T]
}

object DefaultCellParser extends CellParser[String] {
  def extract(c: FancyCell) = Some(c.stringValue)
}

object NumericCellParser extends CellParser[Double] {
  def extract(c: FancyCell): Double = c.numericValue
}
