package models;

import play.api._
import play.api.mvc._

abstract class User {

  var email: String;
  var password: String;
  
}