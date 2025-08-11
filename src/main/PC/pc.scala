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

  val PC_cur = Reg(UInt(64 bits)) init (0)
  val PC_next = Reg(UInt(64 bits)) init (4)

  // Control flow change interfaces

  val nextPC = UInt(64 bits)

  val logic = new stage.Area {

    val sequentialNextPc =
      if (withCompressed) PC_cur + fetch_offset else PC_cur + 4
    nextPC := sequentialNextPc // Default: sequential

    // Priority: exception > flush > jump > sequential
    when(exception.valid) {
      nextPC := exception.payload.vector
    }.elsewhen(flush.valid) {
      nextPC := flush.payload.address
    }.elsewhen(jump.valid) {
      nextPC := jump.payload.target
    }

    PC_cur := nextPC
    PC.PC := PC_cur
    PC.FLUSH := jump.valid || flush.valid || exception.valid
  }
}
