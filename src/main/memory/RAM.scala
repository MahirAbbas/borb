package borb.memory

import spinal.core._
import spinal.lib._
import spinal.core.sim._
import spinal.core

case class RamFetchCmd(addressWidth: Int, idWidth: Int) extends Bundle {
  val address = UInt(addressWidth bits)
  val id = UInt(idWidth bits)
}
case class RamFetchRsp(dataWidth: Int, addressWidth: Int, idWidth: Int)
    extends Bundle {
  val data = Bits(dataWidth bits)
  val address = UInt(addressWidth bits)
  val id = UInt(idWidth bits)
}
case class RamFetchBus(addressWidth: Int, dataWidth: Int, idWidth: Int)
    extends Bundle {
  val cmd = Stream(RamFetchCmd(addressWidth, idWidth))
  val rsp = Stream(RamFetchRsp(dataWidth, addressWidth, idWidth))

  def <<(that: Stream[RamFetchCmd]) = {
    that.valid := this.cmd.valid
    that.payload := this.cmd.payload
    this.cmd.ready := that.ready
  }

  def >>(that: Stream[RamFetchRsp]) = {
    this.rsp.valid := that.valid
    this.rsp.payload := that.payload
    this.rsp.ready := that.ready

  }
}
case class RamWriteCmd(addressWidth: Int, dataWidth: Int) extends Bundle {
  val address = UInt(addressWidth bits)
  val data = Bits(dataWidth bits)
}
case class RamWriteRsp() extends Bundle {
  val success = Bool()
}
case class RamWriteBus(addressWidth: Int, dataWidth: Int) extends Bundle {
  val cmd = Stream(RamWriteCmd(addressWidth, dataWidth))
  val rsp = Stream(RamWriteRsp())
}

class RamReadBus(dataWidth: Int = 64, addressWidth: Int = 64)
    extends Bundle
    with IMasterSlave {
  val address = UInt(addressWidth bits)
  val data = Bits(dataWidth bits)
  val valid = Bool()
  val ready = Bool()

  override def asMaster() = {
    in(data, ready)
    out(valid, address)
  }
}

class UnifiedRam(addressWidth: Int, dataWidth: Int, idWidth: Int)
    extends Component {
  // TODO: Create ports on demand, which are blocking, i.e., if one port is reading/writing, the other can't
  // Vec of writes/reads that plugins add to? maybe make add function? like add read/write port?
  //

  // val read = (slave(new RamReadBus()))

  val io = new Bundle {
    val reads = (new RamFetchBus(addressWidth, dataWidth, idWidth))
    in(reads.cmd)
    out(reads.cmd.ready)
    out(reads.rsp)
    in(reads.rsp.ready)
    // val writes = new RamWriteBus(addressWidth, dataWidth)
    // in(writes.cmd)
    // out(writes.cmd.ready)
    // out(writes.rsp)
    // in(writes.rsp.ready)
  }

  val memSize = 16 KiB
  val addressSize = log2Up(memSize / 8)
  // 8 bytes = 64 bits

  val memory = Mem(Bits(dataWidth bits), wordCount = memSize / 8).simPublic()

  // READING
  io.reads.rsp.payload.data := memory.readSync(
    address = io.reads.cmd.payload.address(addressSize - 1 downto 0),
    enable = io.reads.cmd.valid
  )
  io.reads.cmd.ready := True
  // when(io.writes.cmd.valid) {
  //   io.reads.cmd.ready := False
  // }
  io.reads.rsp.payload.address := io.reads.cmd.payload.address
  // val validReadReg = Reg(io.reads.cmd.valid)
  // validReadReg := io.reads.cmd.valid
  io.reads.rsp.valid := io.reads.cmd.valid

  // memory.write(
  //   address = io.writes.cmd.payload.address(addressSize - 1 downto 0),
  //   data = io.writes.cmd.payload.data,
  //   enable = io.writes.cmd.valid
  // )

}
