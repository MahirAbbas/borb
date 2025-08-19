package borb.fetch

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
// import borb.memory.RamRead

import borb.frontend.PC
import borb.frontend.Decoder.INSTRUCTION
import borb.memory._

case class Fetch(
    stage: CtrlLink,
    addressWidth: Int,
    dataWidth: Int,
    pcStage: CtrlLink
) extends Area {
  val io = new Bundle {
    val readCmd = ((new RamFetchBus(addressWidth, dataWidth, idWidth = 16)))
  }

  // val waitingForRsp = Reg(Bool()) init False
  val logic = new stage.Area {
    io.readCmd.cmd.valid := False
    io.readCmd.cmd.payload.address.assignDontCare()

    INSTRUCTION.assignDontCare()

    val isUpValid = stage.isValid
    val isUpReady = stage.isReady

    val pcVal = stage(PC.PC)
    val instr = stage(INSTRUCTION)

    when(stage.isValid) {
      io.readCmd.cmd.address := up(PC.PC)
      io.readCmd.cmd.valid := True
      when((io.readCmd.cmd.valid && io.readCmd.cmd.ready)) {
        // waitingForRsp := True
      }
      // haltWhen(waitingForRsp)

    }
    when(io.readCmd.rsp.valid) {
      // waitingForRsp := False
      INSTRUCTION := io.readCmd.rsp.data

    }
  }

  // val fifo = StreamFifo()
}
