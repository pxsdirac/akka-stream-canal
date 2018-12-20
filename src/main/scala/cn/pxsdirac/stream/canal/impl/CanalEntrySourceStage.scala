package cn.pxsdirac.stream.canal.impl

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import cn.pxsdirac.stream.canal.CanalSetting
import com.alibaba.otter.canal.client.CanalConnectors
import com.alibaba.otter.canal.protocol.CanalEntry.Entry

import scala.collection.mutable
import scala.collection.JavaConverters._

class CanalEntrySourceStage(setting: CanalSetting) extends GraphStage[SourceShape[Entry]] {
  val out: Outlet[Entry] = Outlet("entry")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    val buffer = mutable.Queue[Entry]()
    val bufferSize = setting.bufferSize
    var batchId: Long = -1

    val connector = {
      val addresses = setting.servers.map { server =>
        val (host, port) = server
        new InetSocketAddress(host, port)
      }
      val connector = if (addresses.size == 1) {
        CanalConnectors.newSingleConnector(addresses(0), setting.destination, setting.username, setting.password)
      } else {
        CanalConnectors.newClusterConnector(addresses.asJava, setting.destination, setting.username, setting.password)
      }
      connector.connect()
      connector.subscribe(setting.subscribeFilter)
      connector
    }

    def getMessage() = setting.enableAutoAck match {
      //wait for 1 second to avoid bust waiting
      case true => connector.get(bufferSize, 1L, TimeUnit.SECONDS)
      case false => connector.getWithoutAck(bufferSize, 1L, TimeUnit.SECONDS)
    }

    def ack() =
      if (setting.enableAutoAck) {
        // do nothing
      } else {
        if (batchId != -1) {
          connector.ack(batchId)
          batchId = -1
        }
      }

    setHandler(
      out,
      new OutHandler {
        override def onPull(): Unit = {
          while (buffer.isEmpty) {
            ack()
            val message = getMessage()
            message.getEntries.forEach { entry =>
              buffer.enqueue(entry)
            }
            batchId = message.getId
          }
          val elem = buffer.dequeue()
          push(out, elem)
        }
      }
    )
  }
  override def shape: SourceShape[Entry] = SourceShape(out)
}
