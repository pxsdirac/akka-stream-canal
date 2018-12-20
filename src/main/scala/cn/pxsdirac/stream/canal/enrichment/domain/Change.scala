package cn.pxsdirac.stream.canal.enrichment.domain

import com.alibaba.otter.canal.protocol.CanalEntry.EventType._
import com.alibaba.otter.canal.protocol.CanalEntry.{Column, RowChange}

import scala.collection.JavaConverters._

trait Change
object Change {
//  sealed trait DML[T]

//  case class Insert[T](row: T) extends DML[T]
//  case class Update[T](before: T, after: T) extends DML[T]
//  case class Delete[T](row: T) extends DML[T]

  sealed trait DML extends Change
  case class Insert(row: Map[String, Column]) extends DML
  case class Update(before: Map[String, Column], after: Map[String, Column]) extends DML
  case class Delete(after: Map[String, Column]) extends DML

  case class DDL(sql: String) extends Change

  def parseFrom(rowChange: RowChange): Seq[Change] = rowChange.getEventType match {
    case INSERT =>
      rowChange.getRowDatasList.asScala
        .map { rowData =>
          rowData.getAfterColumnsList.asScala.map(column => (column.getName, column)).toMap
        }
        .map(Insert.apply)
    case UPDATE =>
      rowChange.getRowDatasList.asScala.map { rowData =>
        val before = rowData.getBeforeColumnsList.asScala.map(column => (column.getName, column)).toMap
        val after = rowData.getAfterColumnsList.asScala.map(column => (column.getName, column)).toMap
        Update(before, after)
      }
    case DELETE =>
      rowChange.getRowDatasList.asScala
        .map { rowData =>
          rowData.getBeforeColumnsList.asScala.map(column => (column.getName, column)).toMap
        }
        .map(Insert.apply)
    case _ =>
      Seq(DDL(rowChange.getSql))
  }
}
