# kasa
Real Estate console based application.

## Project overview
kasa is Java-based real estate management system designed to simplify the process of buying,selling ,and renting properties.

The application provides seprate functionalities for Buyers, Property Owners, and Administrators. it allows owners to list properties, buyers to explore available properties and send requests, and administrators to manage and verify property listings.

The project is being developed using Core Java, Object-Oriented Programming, JDBC, and MySQL.


### 1. User Roles

1.Admin
Verify and reject property listings.
Manage registered users.
View all property listings.
Monitor transactions.

2.Owner
Register and manage an account.
Add new property listings.
Update or remove existing listings.
View buyer requests.

3.Buyer
Register and manage an account.
Search available properties.
Filter properties according to preferences.
Send inquiries to property owners.
Manage favourite properties.


### 2. Architecture

The application follows a layered architecture:
Models: Represent application entities.
DAO: Handle database operations.
Services: Implement business logic.
UI: Handle user interaction.
Enums: Define fixed application values.
Concurrency: Handle simultaneous operations


## DAO (data access object)
1.UserDAO : insert user info to database check where the user belong.
2.PropertyDAO :  add property data to database.
3.TransactionDAO : add transaction data to database and check the transaction status.
4.ReviewDAO : add reviews to database, add rating.
5.PropertyRequestDAO : add property request to database which user requests for buying.