package models

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import play.api.db.DB
import anorm._
import anorm.Sql
import anorm.SqlParser
import play.api.Play.current
import controllers._
import sun.rmi.runtime.Log
import org.mindrot.jbcrypt.BCrypt


class LoginDB{
  
var username = "Ink-Slinger"

  def  userSignUp(email: String, password: String) : String = { //Sign up new user. 
    val salt = BCrypt.gensalt()
    val cryptP = BCrypt.hashpw(password, salt)
    val id = generateId() //Generate unique user id for user
    DB.withConnection{ implicit c =>
    try{  
    val result: Int = SQL("INSERT INTO users(name, email, pass, id, salt) VALUES({username},{email},{cryptP},{id},{salt})").on('username -> username, 'email ->email, 'cryptP -> cryptP, 'id->id, 'salt->salt).executeUpdate()
    return result.toString()
    } catch{
      //case e:MySQLIntegrityConstraintViolationException => Logger.debug("Whoops") //Unable to implement. Exception class not found.
      case e:Exception  => {
        Logger.debug(e.getMessage())
        e.getMessage()
        }
    }
  }
}
  
   def userVerification(email: String, password: String): String = { //verifies user existence and checks password.
    
    DB.withConnection{ implicit c =>
    
    try{
      val salty = SQL("SELECT salt FROM users WHERE users.email = {email} AND users.name ={username}").on('email ->email, 'username -> username).as(SqlParser.str("salt").single)
      val result = SQL("SELECT pass FROM users WHERE users.email = {email} AND users.name ={username}").on('email ->email, 'username -> username).as(SqlParser.str("pass").single)
        if(result == BCrypt.hashpw(password, salty)){
          return "correct password"
        }else {
          return "incorrect password"
        }
      } catch{
        //case e:MySQLIntegrityConstraintViolationException => Logger.debug("Whoops")
        case e:Exception  => {
          Logger.debug(e.getMessage())
          return "email does not exist"
          }
      }
    }
   }
   
   def generateId(): Int = { //Generates user id by incrementing value of last user to sign up.
    
    DB.withConnection{ implicit c =>
    try{
      val result: Int = SQL("SELECT MAX(id) AS id FROM users").as(SqlParser.int("id").single)
      return (result + 1)
      }catch{
      //case e:MySQLIntegrityConstraintViolationException => Logger.debug("Whoops") //Unable to implement. Exception class not found.
      case e:Exception  => {
        return 0
        }
    }
    } 
   }
  
  /*def userMongo()={ //Mongo DB code not implemented. Will move at a later date. 
// Gets a reference to the database
   val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val mongodb = connection(username)
  val essays = mongodb.collectionNames
  
  val collection = mongodb(username)
  */
}


