package com.odenzo.utils.excel

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks

class ExcelFnsTest extends FunSuite with ExcelTestingSupport with ExcelFns {

  test("Index to Address") {
    cellindexesToAddr(0, 0) shouldBe "A0"
    cellindexesToAddr(1, 1) shouldBe "B1"
    cellindexesToAddr(26, 0) shouldBe "A26"
    cellindexesToAddr(0, 26) shouldBe "AA0"
  }

  test("Column Address to Index") {
    colAddrToIndex("A") shouldBe 0
    colAddrToIndex("B") shouldBe 1
    colAddrToIndex("AA") shouldBe 26
    colAddrToIndex("AB") shouldBe 27

  }

}

class ExcelFnProperties extends FunSuite with PropertyChecks with StrictLogging with Matchers   {

  object Fn extends ExcelFns

  test("Generic Properties") {
    val index = 1
    Fn.colAddrToIndex(Fn.colIndexToAddr(index)) shouldBe index

  }
}
