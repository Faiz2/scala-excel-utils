package com.odenzo.utils.excel

import com.typesafe.scalalogging.StrictLogging
import org.scalacheck.Gen
import org.scalatest.FunSuite

class ExcelFnsTest extends FunSuite with ExcelTestingSupport with ExcelFns {

  test("Column Index to Address") {
    colIndexToAddr(0) shouldBe "A"
    colIndexToAddr(1) shouldBe "B"
    colIndexToAddr(25) shouldBe "Z"
    colIndexToAddr(26) shouldBe "AA"
    colIndexToAddr(27) shouldBe "AB"
    colIndexToAddr(52) shouldBe "BA"
    colIndexToAddr(53) shouldBe "BB"
    colIndexToAddr(701) shouldBe "ZZ"

    colIndexToAddr(702) shouldBe "AAA"

  }


  test("Column Address to Index") {

    val letters = ('A' to 'Z')
    val numbers = (0 to 25)
    for ((letter, num) <- letters.zip(numbers)) {

      colShouldBe(letter.toString, num)
    }

    def colShouldBe(a: String, i: Int) = {
      logger.debug(s"Converting $a col address to index, expecting $i")
      colAddrToIndex(a) shouldBe i
    }

    colAddrToIndex("A") shouldBe 0
    colAddrToIndex("a") shouldBe 0
    colAddrToIndex("B") shouldBe 1
    colAddrToIndex("AA") shouldBe 26
    colAddrToIndex("Ab") shouldBe 27
    colAddrToIndex("BA") shouldBe 52
    colAddrToIndex("BB") shouldBe 53
    colAddrToIndex("CA") shouldBe 78
    colAddrToIndex("ZZ") shouldBe 701
    colAddrToIndex("AAA") shouldBe 702
  }


  test("Index to Address") {
    cellindexesToAddr(0, 0) shouldBe "A1"
    cellindexesToAddr(1, 1) shouldBe "B2"
    cellindexesToAddr(26, 0) shouldBe "A27"
    cellindexesToAddr(0, 26) shouldBe "AA1"
  }


}
