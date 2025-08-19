package borb.execute

import spinal.core._
import spinal.lib._
import borb.frontend.AluOp
import scala.collection.mutable.ArrayBuffer
import spinal.lib.misc.plugin.FiberPlugin
import spinal.core.fiber.Fiber
import borb.dispatch._
import _root_.borb.common.MicroCode
import _root_.borb.frontend.Decoder.RD_ADDR
import spinal.lib.misc.pipeline.CtrlLink

trait FunctionalUnit {
  val supportedUOPS: ArrayBuffer[MicroCode.E]
  def getUops: ArrayBuffer[MicroCode.E]
  // def add: MicroCode.E
  def add(op: MicroCode.E): Unit = {
    supportedUOPS += op
  }

  val sel = Bool()
}

trait ExecutionUnitTemplate {
  val sel = Bool()
  val functionalUnits = ArrayBuffer[FunctionalUnit]()

  def getFUs: ArrayBuffer[FunctionalUnit] = functionalUnits

  def add(fu: FunctionalUnit): Unit = {
    functionalUnits += fu
  }

  def writeBack(data: Bits): Unit
  def getRegfile: Unit

}

trait writeBackService {
  def createPort: Unit
}

// class ExecutionUnit(regfile: IntRegFile) extends ExecutionUnitTemplate {
//
//   val writeback = new WriteBackPlugin()
//
//   class WriteBackPlugin extends FiberPlugin {
//     val writePort = master(new RegFileWrite())
//     regfile.newWrite()
//
//     def write(wbStage: CtrlLink, data: Bits) = {
//
//       writePort.valid := True
//       writePort.address := wbStage.up(RD_ADDR).asUInt
//       writePort.data := data
//     }
//   }
//
//   // override def writeBack(data: Bits): Unit = {
//   //   writeback.write(data)
//   // }
// }
