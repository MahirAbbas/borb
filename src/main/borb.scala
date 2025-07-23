package borb

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.frontend._
import borb.dispatch._



class Borb extends Component {
  val pipeline = new StageCtrlPipeline()
  
  val decoder = new Decoder(pipeline.ctrl(1))
  val regfile = new IntRegFile(pipeline.ctrl(2), readSync = false, dataWidth = 64)
  
  

}