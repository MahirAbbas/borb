package borb.frontend

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.misc.plugin._

import scala.collection.mutable.ArrayBuffer

case class JumpCmd() extends Bundle {
  val target = UInt(64 bits)
  val is_jump = Bool()
  val is_branch = Bool()
}

case class FlushCmd() extends Bundle {
  val address = UInt(64 bits)
}

case class ExceptionCmd() extends Bundle {
  val vector = UInt(64 bits)
}

object PC extends AreaObject {
  val PC = Payload(UInt(64 bits))
  val FLUSH = Payload(Bool())
}

case class PC(stage: CtrlLink, withCompressed: Boolean = false) extends Area {

  val jump = Flow(JumpCmd())
  val flush = Flow(FlushCmd())
  val exception = Flow(ExceptionCmd())

  // allows for future support of 'C' extension
  val fetch_offset = withCompressed generate in(UInt(3 bits))

  val PC_cur = Reg(UInt(64 bits)).init(0)

  // Control flow change interfaces

  val logic = new stage.Area {

    val sequentialNextPc =
      if (withCompressed) PC_cur + fetch_offset else PC_cur + 4

    // Priority: exception > flush > jump > sequential
    when(exception.valid) {
      sequentialNextPc := exception.payload.vector
    }.elsewhen(flush.valid) {
      sequentialNextPc := flush.payload.address
    }.elsewhen(jump.valid) {
      sequentialNextPc := jump.payload.target
    }

    PC.PC := PC_cur
    val isDownReady = stage.isReady
    val isDownValid = stage.isValid
    when(down.isReady) {
      PC_cur := PC_cur + 4
      down(PC.PC) := PC_cur
    }


    // PC.FLUSH := jump.valid || flush.valid || exception.valid
  }
}
