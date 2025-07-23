package borb.dispatch

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._

case class RegFileWrite() extends Bundle with IMasterSlave {
  val valid   = Bool()
  val address = UInt(5 bits)
  val data    = Bits(64 bits)
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


case class IntRegFile(readPorts: Int, writePorts: Int, dataWidth: Int) extends Component {
  val io = new Bundle {
    val reads = (0 to readPorts).map(e => slave(new RegFileRead()))
    val writes = (0 to writePorts).map(e => slave(new RegFileWrite()))
  }

  val mem = Mem.fill(32)(Bits(dataWidth bits))

  // Read logic
  // for (i <- 0 until readPorts) {
  //   when(io.reads.addresses(i) === 0) {
  //     io.reads.data(i) := 0
  //   } otherwise {
  //     io.reads.data(i) := mem.readAsync(io.reads.addresses(i))
  //   }
  // }
  
  for(port <- io.reads) {
    when(port.address === 0) {
      port.data := 0
    } otherwise {
      port.data := mem.readAsync(port.address)
    }
  }

  // Write logic
  for (w <- io.writes) {
    when(w.valid && w.address =/= 0) {
      mem.write(w.address, w.data)
    }
  }
}




// case class RegFileIo() extends Bundle{
//   val writes = Vec(writesParameter.map(p => slave(RegFileWrite(rfpp, p.withReady))))
//   val reads = Vec(readsParameter.map(p => slave(RegFileRead(rfpp, p.withReady))))
// }


/**
 * Provide an API which allows to create new read/write ports to a given register file.
 */
trait RegfileService {
  // val elaborationLock = Retainer()

  // def rfSpec : RegfileSpec
  def getPhysicalDepth : Int

  def writeLatency : Int
  def readLatency : Int

  def newRead(withReady : Boolean) : RegFileRead
  def newWrite(withReady : Boolean, sharingKey : Any = null, priority : Int = 0) : RegFileWrite

  def getWrites() : scala.collection.Seq[RegFileWrite] // Used in the hardware simulation to probe all the register writes of the CPU.
}


// case class RegFileWriter(rfSpec : RegfileSpec) extends Bundle{
//   val hartId = Global.HART_ID()
//   val uopId = Decode.UOP_ID()
//   val data = Bits(rfSpec.width bits)
// }

// trait RegFileWriterService{
//   def getRegFileWriters() : Seq[Flow[RegFileWriter]]
// }
