package borb.frontend.prediction

import spinal.core._
import spinal.lib._


case class BtbEntry(tagSize : Int) extends Bundle {
  val tag = ???
  val BIA = ???
  val BTA = ???
  // val predict = UInt(2 bits)

}

case class BHTEntry() extends Bundle {
  val predict = UInt(2 bits)
}

class BTB() extends Area{
  // import borb.frontend.prediction.BtbEntry
  // 4 things
  // read prediction
  // write prediction
  // forget prediction
  
  val size = 256
  val ways = 2
  val index = log2Up(size)
  val tag = 64 - index
  val predict = UInt(2 bits)
  
  val mem = Mem.fill(size)(Vec.fill(ways)(BtbEntry(10)))
  // val RamWrite = mem.RamWriteWithMask(ways)
  val memwrite = Flow(MemWriteCmdWithMask(mem, ways))
  
  // memwrite.valid
  // memwrite.address
  // memwrite.mask
  // for (data <- memwrite.data) {
  //   // data.BIA :=
  // }
  // RamWrite << memwrite
  // RamWrite.payload
  // RamWrite.
  
}
