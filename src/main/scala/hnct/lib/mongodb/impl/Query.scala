package hnct.lib.mongodb.impl

import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters

import scala.reflect.ClassTag

/**
  * Created by tduccuong on 15.11.16.
  */

case class SingleQuery(field: String) {
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

case class JoinQuery(query: Bson) {
  def and(thatQuery: Bson) = Filters.and(query, thatQuery)

  def or(thatQuery: Bson) = Filters.or(query, thatQuery)
}

case class MultipleQueries(queries: Traversable[Bson]) {
  def asAndQuery = Filters.and(queries.toArray:_*)

  def asOrQuery = Filters.or(queries.toArray:_*)
}

object Query {
  implicit def toSingQuery(field: String) = SingleQuery(field)

  implicit def toMultipleQueries(queries: Traversable[Bson]) = MultipleQueries(queries)

  implicit def toJoinQuery(query: Bson) = JoinQuery(query)
}
