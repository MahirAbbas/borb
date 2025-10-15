import spinal.core._
import spinal.core.sim._
import borb._
import borb.memory._

class brobSoc extends Component {
  val brob = new Borb()
  // val ram  = new UnifiedRam(addressWidth = 64, dataWidth = 64, idWidth = 64)
  // brob.io.ramRead <> ram.io.reads
}


object BorbTest extends App {
  SimConfig.withWave.compile(new brobSoc)

}
