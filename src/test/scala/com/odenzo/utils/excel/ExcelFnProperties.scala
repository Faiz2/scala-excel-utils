
package com.odenzo.utils.excel

import com.odenzo.utils.excel.ExcelFns
import com.typesafe.scalalogging.StrictLogging
import org.scalacheck.Gen
import org.scalacheck.Properties

/**
  * This confused IntelliJ Testing
  */
object ExcelFnProperties extends Properties("ExcelFunctionProperties") with StrictLogging {

  import org.scalacheck.Prop._

  val capitalLetterGen = Gen.oneOf('A' to 'Z')
  val lowercaseGen     = Gen.oneOf('a' to 'z')

  val positiveInteger = Gen.choose(0, 100)
  val negativeInteger = Gen.choose(-100, -1)

  object Fn extends ExcelFns

  property("ColAddrAndIndexSymmetricProperty") = forAll(Gen.choose(0, 701)) { i: Int ⇒
      Fn.colAddrToIndex(Fn.colIndexToAddr(i)) == i
 }

}
