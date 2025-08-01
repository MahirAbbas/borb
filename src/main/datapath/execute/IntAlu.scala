package borb.execute

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.decode.Decoder._
import borb.decode.YESNO
import borb.decode.Imm_Select
import borb.decode.ExecutionUnitEnum.ALU
import borb.dispatch.SrcPlugin.IMMED


case class IntAlu(aluNode : CtrlLink) extends Area with FunctionalUnit{
  val SRC1 = borb.dispatch.SrcPlugin.RS1
  val SRC2 = borb.dispatch.SrcPlugin.RS2
  
  override val FUType = borb.decode.ExecutionUnitEnum.ALU
  import borb.execute.Execute._
  import borb.LsuL1.PC.PCVal
  
  val aluNodeStage = new aluNode.Area {
      import borb.dispatch.Dispatch._
      import borb.decode.AluOp
      val result = Bits(64 bits)
      result.assignDontCare()
      when(up(borb.dispatch.Dispatch.SENDTOALU) === True && up.isFiring) {
        result := up(ALUOP).muxDc(
          AluOp.xor      -> (SRC1 ^ SRC2),
          AluOp.or       -> (SRC1 | SRC2),
          AluOp.and      -> (SRC1 & SRC2),
          AluOp.add      -> (SRC1.asSInt + SRC2.asSInt).asBits,
          AluOp.sll      -> (SRC1.asUInt |<< (SRC2(6 downto 0)).asUInt).asBits,
          AluOp.srl      -> (SRC1.asUInt |>> (SRC2(6 downto 0)).asUInt).asBits,
          AluOp.sra      -> (SRC1.asUInt  >> (SRC2(6 downto 0)).asUInt).asBits,
          AluOp.sub      -> (SRC1.asSInt - SRC2.asSInt).asBits,

          AluOp.addw     -> (SRC1.asSInt + SRC2.asSInt)(31 downto 0).resize(64).asBits,
          AluOp.sllw     -> (SRC1.asUInt |<< SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          AluOp.sraw     -> (SRC1.asUInt  >> SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          AluOp.srlw     -> (SRC1.asUInt |>> SRC2(4 downto 0).asUInt)(31 downto 0).resize(64).asBits,
          AluOp.subw     -> (SRC1.asSInt - SRC2.asSInt)(31 downto 0).resize(64).asBits,
          
          AluOp.lui      -> SRC2.asBits,
          AluOp.auipc    -> (SRC2.asUInt + PCVal).asBits
        )
      }
      

      when(up(ALUOP) === AluOp.slt) {
        val slt = (SRC1.asSInt < SRC2.asSInt)
        result := slt.asBits.resize(64)
        
      }
      when(up(ALUOP) === AluOp.sltu) {
        val sltu = (SRC1.asUInt < SRC2.asUInt)
        result := sltu.asBits.resize(64)
      }
      down(RESULT).assignDontCare()
      when(up.isFiring) {
        down(RESULT) := result.asBits
      }
  }
}
