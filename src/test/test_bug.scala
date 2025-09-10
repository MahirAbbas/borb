package borb.test 

import spinal.core._
import spinal.core.sim._
import spinal.lib.misc.pipeline._

object Brarwww666 extends App{
  case class frontEnd() extends Component {
    val pipeline = new StageCtrlPipeline()
    val PC = Payload(UInt(64 bits))
    pipeline.ctrl(0).up(PC) := in.UInt(64 bits)
    val readStage = pipeline.ctrl(2)
    val readHere = new readStage.Area {
      val pc = up(PC)
      pc.simPublic()
    }

    pipeline.build()
  }

  SimConfig.doSim(new frontEnd()){dut =>
    sleep(10)
    println(dut.readHere.pc.toBigInt)
  }
}
