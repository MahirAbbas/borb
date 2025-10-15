package borb.execute

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.frontend.Decoder._
import borb.frontend.YESNO
import borb.frontend.Imm_Select
import borb.frontend.ExecutionUnitEnum.ALU
// import borb.dispatch.SrcPlugin.IMMED

import borb.dispatch._
import borb.common.MicroCode._

object IntAlu extends AreaObject {
  val RESULT = Payload(new RegFileWrite())

}
case class IntAlu(aluNode : CtrlLink) extends Area {
  import IntAlu._
  val SRC1 = borb.dispatch.SrcPlugin.RS1
  val SRC2 = borb.dispatch.SrcPlugin.RS2

  

  // override val FUType = borb.frontend.ExecutionUnitEnum.ALU
  // import borb.execute.Execute._
  import borb.LsuL1.PC.PCVal

  val aluNodeStage = new aluNode.Area {
      import borb.dispatch.Dispatch._
      // import borb.frontend.AluOp
      val result = Bits(64 bits)
      result.assignDontCare()
      when(up(borb.dispatch.Dispatch.SENDTOALU) === True && up.isFiring) {
        result := up(MicroCode).muxDc(
          
          uopXOR      -> (SRC1 ^ SRC2),
          uopOR       -> (SRC1 | SRC2),
          uopAND      -> (SRC1 & SRC2),
          uopADD      -> (SRC1.asSInt + SRC2.asSInt).asBits,
          uopSLL      -> (SRC1.asUInt |<< (SRC2(6 downto 0)).asUInt).asBits,
          uopSRL      -> (SRC1.asUInt |>> (SRC2(6 downto 0)).asUInt).asBits,
          uopSRA      -> (SRC1.asUInt  >> (SRC2(6 downto 0)).asUInt).asBits,
          uopSUB      -> (SRC1.asSInt - SRC2.asSInt).asBits,

          uopADDW     -> (SRC1.asSInt + SRC2.asSInt)(31 downto 0).resize(64).asBits,
          uopSLLW     -> (SRC1.asUInt |<< SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          uopSRAW     -> (SRC1.asUInt  >> SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          uopSRLW     -> (SRC1.asUInt |>> SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          uopSUBW     -> (SRC1.asSInt - SRC2.asSInt)(31 downto 0).resize(64).asBits,

          uopLUI      -> (SRC2.asBits),
          uopAUIPC    -> (SRC2.asUInt + PCVal).asBits
        )
      }


      when(up(MicroCode) === uopSLT) {
        val slt = (SRC1.asSInt < SRC2.asSInt)
        result := slt.asBits.resize(64)

      }
      when(up(MicroCode) === uopSLTU) {
        val sltu = (SRC1.asUInt < SRC2.asUInt)
        result := sltu.asBits.resize(64)
      }
      down(RESULT).assignDontCare()
      when(up.isFiring && LEGAL === YESNO.Y) {
        // down(RESULT) := result.asBits
        down(RESULT).data := result.asBits
        down(RESULT).address := up(RD_ADDR).asUInt
        down(RESULT).valid := (LEGAL === YESNO.Y)
      }
  }
}
