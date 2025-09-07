package test.frontend

import spinal.core._
import spinal.core.sim._
import borb.fetch._
// import borb.LsuL1._
import spinal.lib.misc.pipeline._
import spinal.lib._
import spinal.lib.sim._
import borb.memory._
import borb.fetch.PC
import borb.frontend.Decoder._
import borb.frontend.Decoder

case class front() extends Component {
  val pipeline = new StageCtrlPipeline()
  val pc = new PC(pipeline.ctrl(0))
  val fetch = Fetch(pipeline.ctrl(1), addressWidth = 64, dataWidth = 32)
  val ram = new UnifiedRam(addressWidth = 64, dataWidth = 32, idWidth = 16)
  val decode = new Decoder(pipeline.ctrl(2))
  


  
  val readStage = pipeline.ctrl(3)
  val readHere = new readStage.Area {
    // val pc = up(Fetch.PC_delayed)
    // pc.simPublic()
    val valid          = up(VALID)
    val legal          = up(LEGAL)
    val is_fp          = up(IS_FP)
    val execution_unit = up(EXECUTION_UNIT)
    val rdtype         = up(RDTYPE)
    val rs1type        = up(RS1TYPE)
    val rs2type        = up(RS2TYPE)
    val fsr3en         = up(FSR3EN)
    val immsel         = up(IMMSEL)
    val microcode      = up(MicroCode)
    val is_br          = up(IS_BR)
    val is_w           = up(IS_W)
    val use_ldq        = up(USE_LDQ)
    val use_stq        = up(USE_STQ)

   val signals = Seq(valid, legal,is_fp,execution_unit,rdtype,rs1type,rs2type,fsr3en,immsel,
     microcode,is_br,is_w,use_ldq,use_stq)

  signals.foreach(e => e.simPublic())
  }

  pc.exception.setIdle()
  pc.jump.setIdle()
  pc.flush.setIdle()
  ram.io.reads.cmd << fetch.io.readCmd.cmd
  fetch.io.readCmd.simPublic
  ram.io.reads.rsp >> fetch.io.readCmd.rsp
  fetch.io.readCmd.simPublic()
  pipeline.build()
}


object test_frontend extends App {
  SimConfig.compile(new front()).doSim { dut =>
    dut.clockDomain.forkStimulus(period = 10)


    for(i <- 0 until dut.ram.memory.wordCount) {
      dut.ram.memory.setBigInt(i, (BigInt("00000000", 16)))
    }
    // Pre-load RAM with some data
    val instructions: List[(Long, BigInt)] = List(
      (0,  BigInt("001082B3", 16)),
    )

    for ((address, data) <- instructions) {
      dut.ram.memory.setBigInt(address.toLong, data)
    }

    dut.clockDomain.assertReset()
    dut.clockDomain.deassertReset()
    dut.clockDomain.assertReset()
    dut.clockDomain.deassertReset()

    for(i <- 0 to 10) {
      dut.clockDomain.waitSampling(1)
      println(dut.readHere.valid.toBoolean)
      println(dut.readHere.microcode.toEnum.toString)
      // println(dut.readHere.legal.toEnum.toString)

    }
    
      


    }
  }

