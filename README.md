# Description
AWS Lambda that handles user logins.

# Responsibilities

- This repository has the codebase that enables users to log-in using their username & password.
- It checks if the credentials provided has the correct form as expected by the business rules and responds or acts accordingly.
- It also checks if the user is a new one, i.e. logging in for the first time then provides them with session token using which they can set their own password, which is a must do for them.

# Architecture Diagram

![Login Architecture Diagram](https://user-images.githubusercontent.com/63947196/229899220-d227b649-857f-49f9-b46f-36b28684b1a2.jpg)

# Brief of Working
- For user who is logging in for the first time:
  request : POST | req_body{username,password `/*temporary password that was preset by admin*/`} | res_body{status:200, session_token, isFirstTimeLogin: TRUE}
- For user who has logged in for many times:
  request : POST | req_body{username,password `/*password that was set by user*/`} | res_body{status:200, access_token, isFirstTimeLogin: FALSE}
- The Username and/or password must not contain any whitespaces in between, and must not be empty.
  res_body{status:400, message}
- For incorrect username or password : res_body{status:401, message}
