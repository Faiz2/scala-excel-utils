package com.odenzo.utils.excel

import org.fancypoi.excel.FancyCell
import scala.reflect.runtime.universe._

/**
 * Want to make a cell parsing DSL as end goal, with some default verbs and let use make custom verbs
 */

trait CellParser[T] {

  /**
   * Think about T, Option[T], Either[Error,T] Either[Error,Option[T]] appraoches
   *
   * @param c
   *
   * @return
   */
  def extract(c : FancyCell) : Either[String, T]
}

trait FnCellParser {
  def extract[T : TypeTag](c : FancyCell) : Either[String, T]
}

object DefaultCellParser extends CellParser[String] {
  def extract(c : FancyCell) = Right(c.stringValue)
}

object NumericCellParser extends CellParser[Double] {
  def extract(c : FancyCell) = Right(c.numericValue)
}
