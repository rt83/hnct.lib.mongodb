package hnct.lib.mongodb

import hnct.lib.mongodb.core.BaseM

/**
 * @author tduccuong
 */
case class CategoryM(
  id: Option[String], 
  name: String,
  desc: String,
  imgs: Seq[String]
) extends BaseM(id)

object CategoryM { 
  def apply(name: String, desc: String) = 
    new CategoryM(None, name, desc, Seq()) 
}