package cn.pxsdirac.stream.canal.scaladsl

import akka.NotUsed
import akka.stream.scaladsl.Source
import cn.pxsdirac.stream.canal.CanalSetting
import cn.pxsdirac.stream.canal.impl.CanalEntrySourceStage
import com.alibaba.otter.canal.protocol.CanalEntry.{Entry, RowChange}

object CanalEntrySource {
  def apply(setting: CanalSetting): Source[Entry, NotUsed] =
    Source.fromGraph(new CanalEntrySourceStage(setting))
}
