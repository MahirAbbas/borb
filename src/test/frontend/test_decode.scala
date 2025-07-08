import spinal.core._
import spinal.core.sim._
import spinal.lib.misc.pipeline._
import borb.decode.{Decoder, AluOp, ExecutionUnitEnum, REGFILE, Imm_Select, YESNO}
import borb.decode.DecodeTable
import scala.util.Random

object DecoderRandomTest extends App {
  SimConfig.withWave.compile(new Component {
    val instruction = in port Bits(32 bits)
    // Expose all decode signals as outputs
    val legal          = out port YESNO()
    val is_fp          = out port YESNO()
    val execution_unit = out port ExecutionUnitEnum()
    val rdtype         = out port REGFILE.RDTYPE()
    val rs1type        = out port REGFILE.RSTYPE()
    val rs2type        = out port REGFILE.RSTYPE()
    val fsr3en         = out port YESNO()
    val immsel         = out port Imm_Select()
    val aluop          = out port AluOp()
    val is_br          = out port YESNO()
    val is_w           = out port YESNO()
    val use_ldq        = out port YESNO()
    val use_stq        = out port YESNO()
    // Optionally expose RD, RS1, RS2 if you want to check them too

    val pipeline = new StageCtrlPipeline
    pipeline.ctrl(0)(Decoder.INSTRUCTION) := instruction
    val decoder = new Decoder(pipeline.ctrl(1))
    // Connect all outputs
    legal          := pipeline.ctrl(2)(Decoder.LEGAL)
    is_fp          := pipeline.ctrl(2)(Decoder.IS_FP)
    execution_unit := pipeline.ctrl(2)(Decoder.EXECUTION_UNIT)
    rdtype         := pipeline.ctrl(2)(Decoder.RDTYPE)
    rs1type        := pipeline.ctrl(2)(Decoder.RS1TYPE)
    rs2type        := pipeline.ctrl(2)(Decoder.RS2TYPE)
    fsr3en         := pipeline.ctrl(2)(Decoder.FSR3EN)
    immsel         := pipeline.ctrl(2)(Decoder.IMMSEL)
    aluop          := pipeline.ctrl(2)(Decoder.ALUOP)
    is_br          := pipeline.ctrl(2)(Decoder.IS_BR)
    is_w           := pipeline.ctrl(2)(Decoder.IS_W)
    use_ldq        := pipeline.ctrl(2)(Decoder.USE_LDQ)
    use_stq        := pipeline.ctrl(2)(Decoder.USE_STQ)
    // Optionally connect RD, RS1, RS2

    pipeline.build()
  }).doSim { dut =>
    val decodeTable = DecodeTable.X_table
    val rand = new Random()
    dut.clockDomain.forkStimulus(10)

    for (_ <- 0 until 100) { // Run 20 random tests
      val (instr, expected) = decodeTable(rand.nextInt(decodeTable.length))
      val instrBits = BigInt(instr.value.toString(2), 2)
      dut.instruction #= instrBits
      dut.clockDomain.waitSampling(3) // Wait for pipeline
      // Check all decode signals
      assert(dut.legal.toEnum == expected(0), s"LEGAL mismatch for $instr: got ${dut.legal.toEnum}, expected ${expected(0)}")
      assert(dut.is_fp.toEnum == expected(1), s"IS_FP mismatch for $instr: got ${dut.is_fp.toEnum}, expected ${expected(1)}")
      assert(dut.execution_unit.toEnum == expected(2), s"EXECUTION_UNIT mismatch for $instr: got ${dut.execution_unit.toEnum}, expected ${expected(2)}")
      assert(dut.rdtype.toEnum == expected(3), s"RDTYPE mismatch for $instr: got ${dut.rdtype.toEnum}, expected ${expected(3)}")
      assert(dut.rs1type.toEnum == expected(4), s"RS1TYPE mismatch for $instr: got ${dut.rs1type.toEnum}, expected ${expected(4)}")
      assert(dut.rs2type.toEnum == expected(5), s"RS2TYPE mismatch for $instr: got ${dut.rs2type.toEnum}, expected ${expected(5)}")
      assert(dut.fsr3en.toEnum == expected(6), s"FSR3EN mismatch for $instr: got ${dut.fsr3en.toEnum}, expected ${expected(6)}")
      assert(dut.immsel.toEnum == expected(7), s"IMMSEL mismatch for $instr: got ${dut.immsel.toEnum}, expected ${expected(7)}")
      assert(dut.aluop.toEnum == expected(8), s"ALUOP mismatch for $instr: got ${dut.aluop.toEnum}, expected ${expected(8)}")
      assert(dut.is_br.toEnum == expected(9), s"IS_BR mismatch for $instr: got ${dut.is_br.toEnum}, expected ${expected(9)}")
      assert(dut.is_w.toEnum == expected(10), s"IS_W mismatch for $instr: got ${dut.is_w.toEnum}, expected ${expected(10)}")
      assert(dut.use_ldq.toEnum == expected(11), s"USE_LDQ mismatch for $instr: got ${dut.use_ldq.toEnum}, expected ${expected(11)}")
      assert(dut.use_stq.toEnum == expected(12), s"USE_STQ mismatch for $instr: got ${dut.use_stq.toEnum}, expected ${expected(12)}")
      // println(dut.execution_unit.toEnum)
      println(s"Test passed for instruction $instr")
    }
  }
}
