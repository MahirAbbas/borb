package borb.LsuL1

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import borb.frontend.Decoder.INSTRUCTION


// case class DCacheBus() extends Bundle with IMasterSlave {

// }

// case class RamBus() extends Bundle {
//
//   val ramFetch = master(RamFetch())
//   val ramStore = master(RamStore())
// }

// case class L1CacheBus() extends Bundle with IMasterSlave {
//   val cacheFetchCmd = Stream(L1FetchCmd())
//   val cacheFetchRsp = Stream(L1FetchRsp())
//
//   override def asMaster() = {
//     master(cacheFetchCmd)
//     slave(cacheFetchRsp)
//   }
// }

// object L1Bus extends AreaObject {
//   val ramBus = RamBus()
//   val l1Bus = L1CacheBus()
// }
