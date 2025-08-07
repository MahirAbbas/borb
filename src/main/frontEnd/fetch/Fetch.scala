package borb.frontend.fetch

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.memory.RamRead

import borb.frontend.PC
import borb.frontend.Decoder.INSTRUCTION

case class Fetch(stage : CtrlLink) extends Area {
  val io = new Bundle {
    val readCmd =  out port RamRead(64, 32)
    val instruction = in(Bits(32 bits))
  }

  val is_reading_from_ram = Reg(Bool()) init False
  
  
  val logic = new stage.Area {
    when(up.isFiring) {
      io.readCmd.address := up(PC.PC)
      io.readCmd.valid := True
      when((io.readCmd.valid && io.readCmd.ready) === True) {
        is_reading_from_ram := True
      }
    }
    INSTRUCTION := io.instruction

  }

}
