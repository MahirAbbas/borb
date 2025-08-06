package borb.execute

import spinal.core._
import spinal.lib._
import borb.frontend.AluOp
import scala.collection.mutable.ArrayBuffer
import spinal.lib.misc.plugin.FiberPlugin
import spinal.core.fiber.Fiber
import borb.dispatch._

trait FunctionalUnit {
  def getUops: Seq[AluOp.E]
}

trait ExecutionUnitTemplate {
  val functionalUnits = ArrayBuffer[FunctionalUnit]()

  def getFUs: ArrayBuffer[FunctionalUnit] = functionalUnits

  def add(fu: FunctionalUnit): Unit = {
    functionalUnits += fu
  }

  def writeBack(data: Bits): Unit
}

class ExecutionUnit(regfile : IntRegFile) extends ExecutionUnitTemplate {
  
  regfile.addPort

}