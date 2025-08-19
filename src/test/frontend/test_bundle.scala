package test.frontend

import spinal.core._
import spinal.lib._
// import borb.memory.RamRead
// j
import spinal.core.sim._

class fetchcmd() extends Bundle {
  val address = UInt(32 bits)

}

class fetchrsp() extends Bundle {
  val data = Bits(32 bits)

}

class fetchbus() extends Bundle with IMasterSlave {
  val cmd = Stream(new fetchcmd())
  val rsp = Stream(new fetchrsp())

  override def asMaster() = {
    master(cmd)
    slave(rsp)
  }
}
class test_slave() extends Component {

  val bus = master(new fetchbus())
  bus.cmd.valid := True
  bus.cmd.payload.address := U(2).resized
  bus.rsp.ready := True

  // val testy = new Component {
  //     val mast = (Stream(new RamRead(32, 32)))
  // }
  //
  // val testy2 = new Component {
  //     val slav = (Stream(new RamRead(32, 32)))
  // }
  //
  // // testy2.slav <> testy.mast
  //
  // val testyArea = new Area {
  //     val slav = (Stream(new RamRead(32, 32)))
  // }
  //
  // val testy2Area = new Area {
  //     val mast = (Stream(new RamRead(32, 32)))
  // }
  //
  // testyArea.slav <> testy2Area.mast

}

object test_slave extends App {
  SimConfig.withWave.compile(new test_slave()).doSim { dut =>
    dut.clockDomain.forkStimulus(10)

  }
}
