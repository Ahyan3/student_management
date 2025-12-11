# 📘 Student Record System – Java Swing + MySQL

A simple but complete **Student Record Management System** built with **Java Swing**, **MySQL (XAMPP)**, and an **MVC-style project structure**.  
The system includes Login, Dashboard navigation, Student CRUD management, and history tracking.

**Work by Ryan and ChatGPT**

---

## 🚀 Features

### ✅ Authentication
- Login screen using Swing UI  
- Validates credentials from MySQL database  
- Clean UI with error handling  

### 🎓 Student Management
- Add Student  
- Edit Student  
- Delete Student  
- View All Students  
- Search  

### 📜 History Module
- Logs admin actions  
- Displays record update history  
- Connected to the database  

---

## 🏗️ System Structure (Final Project Structure)

StudentRecordSystem/
│
├── src/
│ └── studentapp/
│ ├── core/
│ │ └── Main.java
│ ├── auth/
│ │ └── LoginForm.java
│ ├── dashboard/
│ │ └── DashboardForm.java
│ ├── panels/
│ │ ├── StudentsPanel.java
│ │ ├── AddStudentPanel.java
│ │ ├── EditStudentPanel.java
│ │ └── HistoryPanel.java
│ ├── database/
│ │ └── DatabaseConnection.java
│ └── models/
│ └── StudentModel.java
│
├── lib/
│ └── mysql-connector-j-9.5.0.jar
│
└── README.md

---

## 🗄️ Database Setup (XAMPP / phpMyAdmin)

1. Start **Apache & MySQL** in XAMPP  
2. Open **phpMyAdmin**  
3. Create database: student_system
4. Create tables:

### **users table (for login)**
```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  password VARCHAR(100)
);

### **students table**
CREATE TABLE students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  age INT,
  course VARCHAR(100),
  year_level VARCHAR(30)
);

### **history table**
CREATE TABLE history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  action VARCHAR(255),
  timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

##🔌 Database Connection Code

### Located in:

src/studentapp/database/DatabaseConnection.java

private static final String URL = "jdbc:mysql://localhost:3306/student_system";
private static final String USER = "root";
private static final String PASS = "";


Make sure your MySQL connector .jar is inside the lib/ folder.


---

##▶️ Running the System

### VS Code

Ensure your main entry file is:

studentapp.core.Main


Run using:

java studentapp.core.Main

Or use Run > Start Debugging if your Java project is configured.


---

## 🔧 Fixes & Improvements Completed

✔ Corrected package structure
✔ Fixed "Could not find or load main class" error
✔ Moved Main.java into proper folder
✔ Setup MySQL connection
✔ Connected panels to main dashboard
✔ Fixed event listeners & navigation buttons
✔ Resolved Swing layout issues
✔ Added error handling
✔ Cleaned up commented code for future use


---

## ✨ Credits

### Developed by:
- Ryan – Implementation, debugging, UI improvements
- ChatGPT – System design, code structure, logic fixes, documentation
