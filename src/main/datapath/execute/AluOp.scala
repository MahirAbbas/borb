package borb.execute

import spinal.core._

object AluOp extends SpinalEnum {
  val ADD, SUB, SLT, SLTU, XOR, OR, AND, SLL, SRL, SRA, LUI, AUIPC = newElement()
}
