package borb

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.frontend._
import borb.dispatch._
import borb.frontend.fetch.Fetch
import borb.memory.UnifiedRam
import borb.datapath.execute.lsu.Memory

class Borb extends Component {
  val pipeline = new StageCtrlPipeline()
  
  val ram = new UnifiedRam(64, 64)
  val pc = new PC(pipeline.ctrl(0))
  val fetch = new Fetch(pipeline.ctrl(1))

  val decoder = new Decoder(pipeline.ctrl(2))
  // val memory = new Memory()

  fetch.io.readCmd <> ram.io.i_port
  // fetch.io.instruction := ram

  val execute = new Area {
    val memory_address = UInt(64 bits)
    val memory_write_data = Bits(64 bits)
    val memory_write_enable = Bool()
  }

  memory.io.address := execute.memory_address
  memory.io.writeData := execute.memory_write_data
  memory.io.writeEnable := execute.memory_write_enable
  ram.io.d_port_read <> memory.io.read
  ram.io.d_port_write <> memory.io.write


  decoder.INSTRUCTION := fetch.io.instruction
  val regfile = new IntRegFile(RamReads = 1, RamWrites = 1, dataWidth = 64)
  
  

}