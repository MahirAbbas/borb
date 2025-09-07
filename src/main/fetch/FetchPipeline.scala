package borb.fetch


import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.memory._


// class frontEndPipeline(pipeline : StageCtrlPipeline) extends Area {
//   val bus = RamFetchBus(addressWidth = 64, dataWidth = 32, idWidth = 16)
//
//   val pc = new PC(pipeline.ctrl(0))
//   val fetch = new Fetch(pipeline.ctrl(1), addressWidth=64, dataWidth=32)
//   bus.cmd := fetch.io.readCmd.cmd
//   fetch.io.readCmd.rsp := bus.rsp
// }
