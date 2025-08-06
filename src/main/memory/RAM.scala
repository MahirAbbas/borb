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
  val reads  = slave(RamRead(64, 64))
  val writes = slave(RamWrite(64, 64))
}

class UnifiedRam(addressWidth: Int, dataWidth: Int) extends Component {
  val io = new Bundle {
    val i_port = slave(RamRead(addressWidth, 32)) // Instruction port is 32-bit
    val d_port_read = slave(RamRead(addressWidth, dataWidth)) // Data port is 64-bit
    val d_port_write = slave(RamWrite(addressWidth, dataWidth)) // Data port is 64-bit
  }

  // The memory itself stores 64-bit words.
  // Addresses from the CPU are byte addresses. The memory is 8-byte (64-bit) word addressable.
  // So we use the upper bits of the address to select the word.
  val memory = Mem(Bits(dataWidth bits), wordCount = 1 << (addressWidth - 3))
  
  val check = new HardMap()

  // Instruction Fetch Port
  // Read a full 64-bit word from memory.
  val i_wordAddress = io.i_port.address >> 3
  val i_fullWord = memory.readSync(i_wordAddress)
  // The instruction can be in the upper or lower half of the 64-bit word.
  // RISC-V instructions are 32-bit aligned, so address bit 2 selects the half-word.
  io.i_port.data := Mux(io.i_port.address(2), i_fullWord(63 downto 32), i_fullWord(31 downto 0))

  // Data Port Read
  val d_wordAddress = io.d_port_read.address >> 3
  io.d_port_read.data := memory.readSync(d_wordAddress)

  // Data Port Write
  when(io.d_port_write.enable) {
    // This simple model doesn't support byte-level writes (yet), only full 64-bit words.
    val d_writeWordAddress = io.d_port_write.address >> 3
    memory.write(d_writeWordAddress, io.d_port_write.data)
  }
}