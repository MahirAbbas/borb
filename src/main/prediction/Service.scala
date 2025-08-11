package borb.frontend.prediction

import spinal.core._


case class LearnCmd() extends Bundle {
  val pcTarget = ???
  val taken = Bool() // was the branch taken
  val badTaken = Bool() // was the taken prediction wrong?
  val badTarget = Bool() // was the predicted target wrong

}

case class ForgetCmd() extends Bundle {
  val PC = ???
}

case class Prediction() extends Bundle {
  val taken = Bool()

  val is_br, is_jal = Bool()

  val predicted_pc = ???
}
