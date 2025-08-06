package borb.frontend.fetch

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.memory.RamRead

import borb.frontend.PC
import borb.frontend.Decoder.INSTRUCTION

case class Fetch(stage : CtrlLink) extends Area {
  val io = new Bundle {
    val readCmd =  RamRead(64, 32)
    val instruction = in(Bits(32 bits))
  }
  
  val logic = new stage.Area {
    io.readCmd.address := up(PC.PC)
    INSTRUCTION := io.instruction
  }

}
