package borb.execute

import spinal.core._
import spinal.lib.misc.pipeline.CtrlLink
import borb.LsuL1._
import borb.frontend.Decoder.USE_STQ
import borb.frontend.YESNO.{Y => Y}
import borb.frontend.Decoder.USE_LDQ
import borb.LsuL1.RamStoreCmd
import borb.frontend.ExecutionUnitEnum._

// case class AGU(node : CtrlLink) extends Area {
//   import borb.dispatch.SrcPlugin._
//
//
//   val logic = new node.Area {
//     // while(!loaded) {
//     //   haltIt()
//     // }
//     // while(!stored) {
//     //   haltIt()
//     // }
//
//
//
//     val memLocation = RS1.asSInt + IMMED.asSInt // FOR LOAD
//     val ramFetchCmd = RamFetchCmd()
//     val ramStoreCmd = RamStoreCmd()
//     val ramFetchRsp = RamFetchRsp()
//
//     ramFetchCmd.address.assignDontCare()
//     when(up(USE_LDQ) === Y) {
//       ramFetchCmd.address := memLocation.asUInt
//     }
//     when(up(USE_STQ) === Y) {
//       ramStoreCmd.address := (RS1.asSInt + IMMED.asSInt).asUInt
//       ramStoreCmd.data    := RS2
//     }
//     // RESULT := loadedResult(frommeory)
//
//
//   }
//   val storeData = RS2 // (FOR STORE INSTRUCTIONS) // SEND TO DCACHE
// }
