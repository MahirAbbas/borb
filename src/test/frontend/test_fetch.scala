package test.frontend

import spinal.core._
import spinal.core.sim._
import borb.fetch._
import borb.LsuL1._
import spinal.lib.misc.pipeline._
import spinal.lib._
import borb.memory._
import borb.frontend.PC
import borb.frontend.AluOp.add

case class frontEnd() extends Component {
  val pipeline = new StageCtrlPipeline()
  val pc = new PC(pipeline.ctrl(0))
  pc.PC_cur.simPublic()
  val fetch = Fetch(
    pipeline.ctrl(1),
    addressWidth = 64,
    dataWidth = 32,
    pcStage = pipeline.ctrl(0)
  )
  val ram = new UnifiedRam(addressWidth = 64, dataWidth = 32, idWidth = 16)
  val readStage = pipeline.ctrl(2)
  val readHere = new readStage.Area {}

  pc.exception.setIdle()
  pc.jump.setIdle()
  pc.flush.setIdle()
  // fetch.waitingForRsp.simPublic
  ram.io.reads.cmd << fetch.io.readCmd.cmd
  fetch.io.readCmd.simPublic
  // fetch.io.readCmd.rsp >> ram.io.reads.rsp
  ram.io.reads.rsp >> fetch.io.readCmd.rsp

  // fetch.pcIssued.simPublic()
  fetch.io.readCmd.simPublic()
  pc.logic.isDownReady.simPublic
  pc.logic.isDownValid.simPublic
  fetch.logic.isUpReady.simPublic
  fetch.logic.isUpValid.simPublic
  fetch.logic.pcVal.simPublic
  fetch.logic.instr.simPublic

  pipeline.build()
}

object test_frontEndCompile extends App {
  SimConfig.compile(new frontEnd)
}

object test_fetch extends App {
  SimConfig.withWave
    .compile(new frontEnd())
    .doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)

      // Pre-load RAM with some data
      val instructions: List[(Long, BigInt)] = List(
        (0,  BigInt("11223344", 16)),
        (4,  BigInt("55667788", 16)),
        (8,  BigInt("99aabbcc", 16)),
        (12, BigInt("ddeeff00", 16)),
        (16, BigInt("DEADBEEF", 16)),
        (20, BigInt("CAFEBABE", 16)),
        (24, BigInt("0BADF00D", 16)),
        (28, BigInt("12345678", 16)),
        (32, BigInt("89ABCDEF", 16)),
        (36, BigInt("0F1E2D3C", 16)),
        (40, BigInt("4B5A6978", 16)),
        (44, BigInt("90ABC123", 16)),
        (48, BigInt("76543210", 16)),
        (52, BigInt("FEEDFACE", 16)),
        (56, BigInt("C001D00D", 16)),
        (60, BigInt("AAAAAAAA", 16))
      )

      for ((address, data) <- instructions) {
        dut.ram.memory.setBigInt(address.toLong, data)
      }

      // dut.clockDomain.waitSampling(0)
      dut.clockDomain.assertReset()
      // dut.clockDomain.waitSampling(5)
      dut.clockDomain.deassertReset()
      dut.clockDomain.assertReset()
      dut.clockDomain.deassertReset()
      dut.clockDomain.waitSampling(1)

      for ((address, data) <- instructions) {
        dut.clockDomain.waitSampling(1)
        print(s"${dut.fetch.logic.pcVal.toBigInt}  ")
        println(s"Data should be ${data.toLong.toHexString}, got ${dut.fetch.logic.instr.toLong.toHexString}")

        // assert(dut.fetch.io.readCmd.rsp.data.toBigInt == data, s"Data should be ${data.toLong.toHexString}, got ${dut.fetch.io.readCmd.rsp.data.toLong.toHexString}")
      }
    }
}
