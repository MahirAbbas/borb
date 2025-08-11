package borb.datapath.execute.lsu

import spinal.core._
import spinal.lib._
import borb.memory.{RamRead, RamWrite}

class Memory extends Component {
  val io = new Bundle {
    val read = master(RamRead(64, 64))
    val write = master(RamWrite(64, 64))
    val address = in(UInt(64 bits))
    val writeData = in(Bits(64 bits))
    val writeEnable = in(Bool())
    val readData = out(Bits(64 bits))
  }

  io.read.address := io.address
  io.readData := io.read.data

  io.write.address := io.address
  io.write.data := io.writeData
  // io.write.enable := io.writeEnable
}
