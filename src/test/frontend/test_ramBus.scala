package borb.test

import spinal.core._
import spinal.lib._
import spinal.core.sim._
import scala.util.Random

import borb.memory._
import borb.LsuL1._ // Import RamFetchBus and RamStoreBus

// class cachelessComponent() extends Component {
//   val ramRead = new RamFetchBus(addressWidth = 64, dataWidth = 64)
//   val ramWrite = new RamWriteBus(addressWidth = 64, dataWidth = 64)

//   // val ramRead = master(new RamReadBus())

//   ramRead.simPublic()
//   ramWrite.simPublic()

//   val ram = new UnifiedRam(64, 64)

//   // ramRead <> ram.read

//   // Manual connection for ramRead
//   ram.io.reads.cmd.valid := ramRead.cmd.valid
//   ram.io.reads.cmd.payload.address := ramRead.cmd.payload.address
//   ramRead.cmd.ready := ram.io.reads.cmd.ready
//   //
//   ramRead.rsp.valid := ram.io.reads.rsp.valid
//   ramRead.rsp.payload.data := ram.io.reads.rsp.payload.data
//   ram.io.reads.rsp.ready := ramRead.rsp.ready
//   //
//   // // Manual connection for ramWrite
//   ram.io.writes.cmd.valid := ramWrite.cmd.valid
//   ram.io.writes.cmd.payload.address := ramWrite.cmd.payload.address
//   ram.io.writes.cmd.payload.data := ramWrite.cmd.payload.data
//   ramWrite.cmd.ready := ram.io.writes.cmd.ready
// }

// object compileCacheless extends App {
//   SimConfig.compile(new cachelessComponent)
// }

// object testCacheless extends App {
//   SimConfig.withWave.doSim(new cachelessComponent()) { dut =>
//     dut.clockDomain.forkStimulus(period = 10)

//     // Initialize signals
//     dut.ramRead.cmd.valid #= false
//     dut.ramRead.cmd.payload.address #= 0
//     dut.ramRead.rsp.ready #= true
//     dut.ramWrite.cmd.valid #= false
//     dut.ramWrite.cmd.payload.address #= 0
//     dut.ramWrite.cmd.payload.data #= 0

//     dut.clockDomain.waitSampling(
//       10
//     ) // Wait a few cycles for reset/initialization

//     // Helper functions for bus operations
//     def write(address: BigInt, data: BigInt) = {
//       dut.ramWrite.cmd.valid #= true
//       dut.ramWrite.cmd.payload.address #= address
//       dut.ramWrite.cmd.payload.data #= data
//       dut.clockDomain.waitSampling()
//       dut.ramWrite.cmd.valid #= false
//     }

//     def read(address: BigInt): BigInt = {
//       dut.ramRead.cmd.valid #= true
//       dut.ramRead.cmd.payload.address #= address
//       dut.clockDomain.waitSampling()
//       dut.ramRead.cmd.valid #= false
//       dut.clockDomain.waitSamplingWhere(dut.ramRead.rsp.valid.toBoolean)
//       dut.ramRead.rsp.payload.data.toBigInt
//     }

//     println("Starting test: Write and Read back verification")

//     val testData = List(
//       (BigInt(0x100), BigInt("DEADBEEF", 16)),
//       (BigInt(0x108), BigInt("CAFEBABE", 16)),
//       (BigInt(0x110), BigInt("BADDCAFE", 16))
//     )

//     // Write data
//     for ((addr, data) <- testData) {
//       println(
//         s"Writing 0x${data.toString(16)} to address 0x${addr.toString(16)}"
//       )
//       write(addr, data)
//       dut.clockDomain.waitSampling()
//     }

//     dut.clockDomain.waitSampling(
//       2
//     ) // Wait a couple of cycles for writes to settle

//     // Read and verify data
//     println(
//       s"get memory value at 0x${testData(0)._1.toLong.toHexString}: ${dut.ram.memory
//           .getBigInt(256)
//           .toLong
//           .toHexString}, expected ${testData(0)._2.toLong.toHexString}"
//     )

//     for ((addr, data) <- testData) {
//       println(s"Reading from address 0x${addr.toString(16)}")
//       val readData = read(addr)
//       dut.clockDomain.waitSampling()
//       dut.clockDomain.waitSampling()
//       println(s"Read 0x${readData.toString(16)}")
//       assert(
//         readData == data,
//         s"Data mismatch at address 0x${addr.toString(16)}: Expected 0x${data
//             .toString(16)}, got 0x${readData.toString(16)}"
//       )
//     }

//     println("\nTest case with random data")
//     val random = new Random()
//     val randomTestData = (0 until 10).map { _ =>
//       val addr = BigInt(random.nextInt(64)) * 8 // Word-aligned addresses
//       val data = BigInt(64, random)
//       (addr, data)
//     }.toMap

//     for ((addr, data) <- randomTestData) {
//       write(addr, data)
//     }

//     dut.clockDomain.waitSampling(2)

//     for ((addr, data) <- randomTestData) {
//       val readData = read(addr)
//       println(s"Expected ${data.toString(16)}, Got : ${readData.toString(16)}")
//       // assert(readData == data, s"Random Test Failed at address 0x${addr.toString(16)}: Expected 0x${data.toString(16)}, got 0x${readData.toString(16)}")
//     }

//     println("\nAll tests passed!")
//   }
// }
