package borb.frontend

import spinal.core._


object Priority {
  val predicted = 1 // branch prediction
  val flush = 2 // mispredict/CSR write
  val interrupt = 3 // interrupts / exceptions
}
