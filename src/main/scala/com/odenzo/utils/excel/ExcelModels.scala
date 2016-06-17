package com.odenzo.utils.excel

/**
 * Small collection of Type-Safe style things. ADT or Type not sure yet.
 */
object ExcelModels { // Using type and case classes for testing
  type ColAddr = String
  type ColIndx = Int
  type RowIndx = Int
}

/**
 * Represents a Column Address, e.g. A, AA, ZA
 *
 * @param l Currently system only handle A...ZZ correctly.
 */
case class ColAddr(l: String) {
  require(l.length == 1 || l.length == 2)
}

case class ColIndx(i: Int)

case class RowIndx(i: Int)

