package borb.memory

import spinal.core._
import spinal.lib._



case class RamRead(addressWidth: Int, dataWidth: Int) extends Bundle with IMasterSlave {
  val address = UInt(addressWidth bits)
  val data = Bits(dataWidth bits)
  val ready = Bool()
  val valid = Bool()

  override def asMaster(): Unit = {
    out(address, valid)
    in(data, ready)
  }
}

case class RamWrite(addressWidth: Int, dataWidth: Int) extends Bundle with IMasterSlave {
  val address = UInt(addressWidth bits)
  val data = Bits(dataWidth bits)
  val valid = Bool()
  val ready = Bool()

  override def asMaster(): Unit = {
    out(address, data, valid)
    in(ready)
  }
}

case class RamIO() extends Bundle {
  val reads  = slave(RamRead(addressWidth = 64, dataWidth = 32))
  val writes = slave(RamWrite(addressWidth = 64, dataWidth = 32))
}

class UnifiedRam(addressWidth: Int, dataWidth: Int) extends Component {
  //TODO: Create ports on demand, which are blocking, i.e., if one port is reading/writing, the other can't
  // Vec of writes/reads that plugins add to? maybe make add function? like add read/write port?
  //

  val io = new RamIO()

  // The memory itself stores 64-bit words.
  // Addresses from the CPU are byte addresses. The memory is 8-byte (64-bit) word addressable.
  // So we use the upper bits of the address to select the word.
  val memory = Mem(Bits(dataWidth bits), wordCount = 4 KiB)
  
  
  //val check = new HardMap()

  // Instruction Fetch Port

  io.reads.ready := True
  io.writes.ready := True
  io.reads.data := memory.readSync(io.reads.address, enable = io.reads.valid)

  when(io.writes.valid) {
    io.reads.ready := False
  }

  when(io.reads.valid) {
    io.writes.ready := False
  }

  memory.write(address = io.writes.address, data = io.writes.data, enable = io.writes.valid)

}
