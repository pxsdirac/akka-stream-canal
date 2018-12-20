package cn.pxsdirac.stream.canal.scaladsl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cn.pxsdirac.stream.canal.CanalSetting
import cn.pxsdirac.stream.canal.enrichment.domain.Change
import cn.pxsdirac.stream.canal.enrichment.domain.Change.Insert

object RowChangeSourceTest extends App {
  implicit val system = ActorSystem("canal")
  implicit val materializer = ActorMaterializer()
  val setting = CanalSetting(servers = List(("127.0.0.1", 11111)), destination = "example")
  CanalRowChangeSource(setting)
    .map(Change.parseFrom)
    .mapConcat(_.toList)
    .collect {
      case Insert(row) => row
    }
    .map { row =>
      row.map { case (k, c) => (k, c.getValue) }
    }
    .runForeach(println)
}
