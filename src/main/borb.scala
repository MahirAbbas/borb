package borb

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.frontend._
import borb.dispatch._
import borb.fetch._
import borb.memory._
import borb.execute._
// import borb.datapath.execute.lsu.Memory

class Borb extends Component {
  val io = new Bundle {
    val ramRead = new RamFetchBus(addressWidth = 64, dataWidth = 64, idWidth = 16)
  }
  // PIPELINE
  // FETCH
  // DECODE
  // EXECUTE
  // WRITEBACK
  // MEMORY
  val pipeline = new StageCtrlPipeline()

  val pc = new PC(pipeline.ctrl(0))
  val fetch = new Fetch(pipeline.ctrl(1), addressWidth = 64, dataWidth = 64)
  io.ramRead.cmd := fetch.io.readCmd.cmd
  val decoder = new Decoder(pipeline.ctrl(2))
  val srcPlugin = new SrcPlugin(pipeline.ctrl(3))
  val dispatcher = new Dispatch(pipeline.ctrl(4))
  val hazardRange = Array(0,1,2,3,4).map(e => pipeline.ctrls.getOrElseUpdate(e, pipeline.ctrl(0)))
  val hazardChecker = new HazardChecker(hazardRange)
  val intalu = new IntAlu(pipeline.ctrl(5))

  import borb.execute.IntAlu._


  val write = pipeline.ctrl(6)

  val writeback = new write.Area {
    srcPlugin.regfile.io.writer.address := RESULT.address
    srcPlugin.regfile.io.writer.data := RESULT.data
    srcPlugin.regfile.io.writer.valid := RESULT.valid
  }
  
  // val writeback = new pipeline.ctrl(6).Area {
  //
  // }

  // val execute = new Area {
  //   val memory_address = UInt(64 bits)
  //   val memory_write_data = Bits(64 bits)
  //   val memory_write_enable = Bool()
  // }

  // memory.io.address := execute.memory_address
  // memory.io.writeData := execute.memory_write_data
  // memory.io.writeEnable := execute.memory_write_enable
  // ram.io.d_port_read <> memory.io.read
  // ram.io.d_port_write <> memory.io.write

  // Decoder.INSTRUCTION := fetch.io.instruction
  // val regfile = new IntRegFile(RamReads = 1, RamWrites = 1, dataWidth = 64)
  //
  //
  pipeline.build()
}
