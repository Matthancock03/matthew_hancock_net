
 package controllers

import play.api._
import play.api.mvc._
import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models._
import models.LoginDB
import play.api.data.Forms._
import play.api.data._
import org.mindrot.jbcrypt.BCrypt
import play.mvc.Http.Session

object Application extends Controller {
  
 case class signUp(email : String, password: String, passwordVerification: String)
 case class loginData(email: String, password: String)

 val sqlDriver = new LoginDB();
 
 val loginForm = Form(
      mapping(
          "email" -> email,
          "password" -> nonEmptyText
          )(loginData.apply)(loginData.unapply)
          )
          
  val signUpForm = Form(
      mapping(
          "email" -> email,
          "password" -> nonEmptyText,
          "passwordVerification" -> nonEmptyText
          )(signUp.apply)(signUp.unapply)
          )
           
  def loginValidation () = Action{ implicit request =>
     loginForm.bindFromRequest.fold(
         formWithErrors => BadRequest("Invalid submission"),
         user => {
           val result = sqlDriver.userVerification(user.email, user.password)
             if(result == "correct password"){
             Ok("Welcome back :" + user.email).withSession("email" -> user.email)
             }
             else if(result == "incorrect password")
             {Ok(views.html.login("Incorrect Password"));}
             else{Ok(views.html.login("No User Registered By That Email. Please Sign Up"));}
         })
         }
        
 
 
 def signUpUser() = Action{ implicit request =>
     signUpForm.bindFromRequest.fold(
         formWithErrors => BadRequest("Invalid submission"),
         user => {  
         if(sqlDriver.userVerification(user.email, user.password) != "email does not exist"){
           Ok(views.html.login("Account Already Exists For That Email. Please Sign In"));
         }else if(user.password != user.passwordVerification){
              Ok(views.html.login("Account Already Exists For That Email. Please Sign In"));
         }
         else{  
         val result = sqlDriver.userSignUp(user.email, user.password)
         Ok("created account for :" + user.email)}
         })
 }
 
  def login = Action { request =>
    request.session.get("matt").map{ // string should be email, but I disabled. 
      user => Ok("Wecome back " + user)
    }.getOrElse{
  	Ok(views.html.login(""));}
  }  
 }