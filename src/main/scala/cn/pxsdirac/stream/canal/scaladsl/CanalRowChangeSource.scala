package cn.pxsdirac.stream.canal.scaladsl

import akka.NotUsed
import akka.stream.scaladsl.Source
import cn.pxsdirac.stream.canal.CanalSetting
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange

object CanalRowChangeSource {
  def apply(setting: CanalSetting): Source[RowChange, NotUsed] =
    CanalEntrySource(setting)
      .map { entry =>
        RowChange.parseFrom(entry.getStoreValue)
      }
}
