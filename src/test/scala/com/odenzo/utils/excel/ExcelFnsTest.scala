package com.odenzo.utils.excel

import com.typesafe.scalalogging.StrictLogging
import org.scalacheck.Gen
import org.scalatest.FunSuite

class ExcelFnsTest extends FunSuite with ExcelTestingSupport with ExcelFns {

  test("Index to Address") {
    cellindexesToAddr(0, 0) shouldBe "A0"
    cellindexesToAddr(1, 1) shouldBe "B1"
    cellindexesToAddr(26, 0) shouldBe "A26"
    cellindexesToAddr(0, 26) shouldBe "AA0"
  }

 test("Column Index to Address") {
    colIndexToAddr(0) shouldBe "A"
    colIndexToAddr(1) shouldBe "B"
    colIndexToAddr(-1) shouldBe 1


  }

  test("Column Address to Index") {
    colAddrToIndex("A") shouldBe 0
    colAddrToIndex("a") shouldBe 0
    colAddrToIndex("B") shouldBe 1
    colAddrToIndex("AA") shouldBe 26
    colAddrToIndex("Ab") shouldBe 27
    colAddrToIndex("AAA") shouldBe 27

  }

}

import org.scalacheck.Properties

/**
  * This confused IntelliJ Testing
  */
object ExcelFnSpecification extends Properties("ExcelFunctionProperties") with StrictLogging {

  import org.scalacheck.Prop._

  val capitalLetterGen = Gen.oneOf('A' to 'Z')
  val lowercaseGen     = Gen.oneOf('a' to 'z')

  val positiveInteger  = Gen.choose(0, 100)
  val negativeInteger  = Gen.choose(-100, -1)

  object Fn extends ExcelFns

  property("symmetricProperty") = forAll(Gen.choose(0, 1000)) { i: Int ⇒
    (i >= 0) ==> (Fn.colAddrToIndex(Fn.colIndexToAddr(i)) == i)
    //(i < 0) ==>  1 // expect to fail this
  }
}
