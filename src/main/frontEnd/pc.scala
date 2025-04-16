package borb.frontend

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._

case class JumpCmd() extends Bundle {
  val fault = Bool()
  val target = PC()
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

case class PC(pipeline: StageCtrlPipeline) extends Area {

  val PC_cur = Reg(UInt(64 bits))
  val PC_next = Reg(UInt(64 bits))

case class jumpSpec(bus : Flow[JumpCmd]) extends Composite(bus)

val jumps = ArrayBuffer[jumpSpec]


  val logic = new pipeline(0).Area  {
    PC_next += 4
    PC_cur := PC_next
  }


}
