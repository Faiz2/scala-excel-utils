package com.odenzo.utils.excel

/**
 * Small collection of Type-Safe style things. ADT or Type not sure yet.
 */
object ExcelModels { // Using type and case classes for testing
  type ColAddr = String
  type ColIndx = Int
  type RawAddr = Int
  type RowIndx = Int
}

/**
 * Represents a Column Address, e.g. A, AA, ZA. Should be uppercase
 * Note that Column A maps to Column Index 0
 * @param a Currently system only handle A...ZZ correctly in all functions
 */
case class ColAddr(a: String) {
  require(a.length == 1 || a.length == 2)
  val addr = a.toUpperCase
}

/**
  *
  * @param addr 1 Based Row Address (e.g. RowIndex(0) === RowAddr(1)
  */
case class RowAddr(addr:Int) {

}

/**
  *
  * @param i Zero-Based column index
  */
case class ColIndx(i: Int)


/**
  *
  * @param i Zero based row index
  */
case class RowIndx(i: Int)

