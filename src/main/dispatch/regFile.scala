package borb.dispatch

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.misc.plugin._
import scala.collection.mutable.ArrayBuffer

case class RegFileWrite() extends Bundle with IMasterSlave {
  val valid = Bool()
  val address = UInt(5 bits)
  val data = Bits(64 bits)
  override def asMaster(): Unit = {
    out(valid, address, data)
  }
}

case class RegFileRead() extends Bundle with IMasterSlave {
  val valid = Bool()
  val address = UInt(5 bits)
  val data = Bits(64 bits)

  override def asMaster(): Unit = {
    in(data)
    out(address, valid)

  }
}

object IntRegFile extends AreaObject {
  val RegFile_RS1 = Payload(Bits(64 bits))
  val RegFile_RS2 = Payload(Bits(64 bits))
}

case class IntRegFile(dataWidth: Int) extends Component {

  // val readers: ArrayBuffer[RegFileRead] = ArrayBuffer(RegFileRead())
  // val writers: ArrayBuffer[RegFileWrite] = ArrayBuffer(RegFileWrite())
  //
  // def newRead() = {
  //   readers += new RegFileRead()
  // }
  // def newWrite() = {
  //   writers += new RegFileWrite()
  // }
  //
  // val io = new Bundle {
  //   val reads = Vec(readers.map(e => slave(new RegFileRead())))
  //   val writes = Vec(writers.map(e => slave(new RegFileWrite())))
  // }

  val io = new Bundle {
    val reader = slave(new RegFileRead())
    val writer = slave(new RegFileWrite())
  }
  val mem = Mem.fill(32)(Bits(dataWidth bits))

  io.reader.data := mem.readSync(address = io.reader.address, enable = io.reader.valid)

  mem.write(address = io.writer.address, enable = io.writer.valid, data = io.writer.data)


  // Read logic
  // for (port <- io.reads) {
  //   when(port.address === 0) {
  //     port.data := 0
  //   } otherwise {
  //     port.data := mem.readAsync(port.address)
  //   }
  // }
  //
  // // Write logic
  // for (w <- io.writes) {
  //   when(w.valid && w.address =/= 0) {
  //     mem.write(w.address, w.data)
  //   }
  // }
}
