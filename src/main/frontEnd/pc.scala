package borb.frontend

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.misc.plugin._

import scala.collection.mutable.ArrayBuffer

case class JumpCmd(priority : Int) extends Bundle {
  val fault = Bool()
  // val target = PC()
  val is_jump = Bool()
  val is_branch = Bool()
}

case class FlushCmd() extends Bundle {
  val flush = Bool()
  val address = UInt(64 bits)
}

case class InterruptCmd() extends Bundle {

}

case class ExceptionCmd() extends Bundle {

}
object PC extends AreaObject {
  val PC = UInt(64 bits)
}

case class PC(pipeline: StageCtrlPipeline) extends FiberPlugin {

  val PC_cur = Reg(UInt(64 bits))
  val PC_next = Reg(UInt(64 bits))


  val jumps = ArrayBuffer[Flow[JumpCmd]]()
  def newJumpInterface(priority : Int) = {
    jumps += Flow(JumpCmd(priority))
  }
  
  val sortedByPriority = jumps.sortWith(_.priority > _.priority)
  val valids = sortedByPriority.map(e => e.valid)

  val first = PriorityMux(valids)


  val logic = new pipeline(0).Area  {
    PC_next += 4
    PC_cur := first.target
    
    PC.PC := PC_cur
  }


}
