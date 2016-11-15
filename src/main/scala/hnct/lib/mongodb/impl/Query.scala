package hnct.lib.mongodb.impl

import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters

import scala.reflect.ClassTag

/**
  * Created by tduccuong on 15.11.16.
  */

case class Query(field: String) {
  def ===[T](value: T)(implicit t : ClassTag[T]) =
    Filters.eq[T](field, value)

  def >>[T](value: T)(implicit t : ClassTag[T]) =
    Filters.gt[T](field, value)

  def >>=[T](value: T)(implicit t : ClassTag[T]) =
    Filters.gte[T](field, value)

  def <<[T](value: T)(implicit t : ClassTag[T]) =
    Filters.lt[T](field, value)

  def <<=[T](value: T)(implicit t : ClassTag[T]) =
    Filters.lte[T](field, value)

  def in[T](values: Traversable[T])(implicit t : ClassTag[T]) =
    Filters.in[T](field, values.toArray:_*)
}

object Query {
  implicit def toQuery(field: String) = new Query(field)

  implicit def andSeq(filters: Traversable[Bson]) =
    Filters.and(filters.toArray:_*)
}
