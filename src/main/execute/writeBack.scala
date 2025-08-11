package borb.execute

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.misc.plugin._
import borb.dispatch._

// class WriteBackService(val euCount: Int) extends Plugin[ExecutePipeline] {
//   val writePorts = Vec(master(new RegFileWrite()), euCount)
//
//   override def build(pipeline: ExecutePipeline): Unit = {
//     for ((writePort, eu) <- writePorts.zip(pipeline.eus)) {
//       eu.res.driveRegFile(writePort)
//     }
//   }
// }
//
