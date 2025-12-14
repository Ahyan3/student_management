# 📘 Student Record System  
**Java Swing • MySQL • Modern Desktop UI**

A modernized Student Record Management System built using Java Swing and MySQL (XAMPP), designed with a website-inspired UI, clean navigation, and a scalable structure.  
The system focuses on usability, clean design, and real-world functionality such as logging, exporting, and dashboard analytics.

**Developed by Ryan, with design & system guidance from ChatGPT**

## 🧠 System Overview

This application allows administrators to securely manage student records through a login system, a central dashboard, and feature-rich modules such as Student Management and History Tracking.

Special focus was given to:

- UI/UX consistency
- Modern desktop design (website-like layout)
- Maintainable and extendable code

## 🚀 Features

### 🔐 Authentication
- Secure login using MySQL database
- Website-style login layout (split panel design)
- Input validation & user feedback
- Clean, modern Swing UI using Poppins font

### 🏠 Main Dashboard
- Sidebar navigation (Home, Student, History, Settings)
- Website-inspired layout
- Card-based dashboard overview
- Future-ready structure for dark mode & analytics
- Role-ready (admin/user expandable)

### 🎓 Student Management
- Add, Update, Delete student records
- Field validation (Student ID, Year Level, etc.)
- Table row selection auto-fills form
- Locked table (no accidental edits)
- Clean form layout with modern buttons

### 📤 Export Features
- Export student records to CSV
- Export student records to PDF
- Uses JFileChooser for user-selected save location

### 📜 History / Logs
- Automatic logging of:
  - Add actions
  - Update actions
  - Delete actions
- Stored in database with timestamps
- Used for auditing and monitoring changes

### 🎨 UI & UX Improvements (Major Upgrade)
- Website-inspired layout (left/right panels)
- Rounded buttons and inputs
- Consistent color palette (blue/gray theme)
- Improved spacing & alignment
- Sidebar navigation with hover effects
- Transparent & soft UI elements
- Font consistency using Poppins

The system now feels closer to a real production desktop application, not a basic Swing demo.

## 🏗️ Project Structure

```
StudentRecordSystem/
│
├── src/
│   └── studentapp/
│       ├── core/
│       │   └── Main.java
│       ├── auth/
│       │   └── LoginForm.java
│       ├── home/
│       │   └── HomePanel.java
│       ├── student/
│       │   └── StudentPanel.java
│       ├── history/
│       │   └── HistoryPanel.java
│       ├── settings/
│       │   └── SettingsPanel.java
│       ├── database/
│       │   └── DatabaseConnection.java
│
├── lib/
│   └── mysql-connector-j-9.5.0.jar
│
├── resources/
│   └── fonts/
│       ├── Poppins-Regular.ttf
│       └── Poppins-Bold.ttf
│
└── README.md
```

## 🗄️ Database Setup (MySQL / XAMPP)

1. Start Apache & MySQL in XAMPP
2. Open phpMyAdmin
3. Create database:

```sql
CREATE DATABASE student_system;
```

### 👤 Users Table (Login)

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20)
);
```

### 🎓 Students Table

```sql
CREATE TABLE students (
  student_id VARCHAR(20) PRIMARY KEY,
  fullname VARCHAR(100),
  course VARCHAR(100),
  year VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 📜 Update Logs Table

```sql
CREATE TABLE update_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  student_id VARCHAR(20),
  action VARCHAR(50),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 🔌 Database Connection
File: `src/studentapp/database/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/student_system";
private static final String USER = "root";
private static final String PASS = "";
```

Make sure your MySQL Connector JAR is placed inside the `lib/` folder and added to the project classpath.

## ▶️ Running the Application

**Entry Point**

```
studentapp.core.Main
```

**Run via Terminal**

```bash
java studentapp.core.Main
```

Or simply use Run / Debug in VS Code if Java is configured.

## 🔧 Fixes & Enhancements Completed
- Refactored UI layout to modern style
- Improved login screen design
- Redesigned dashboard navigation
- Added CSV & PDF export
- Added student action logging
- Fixed table edit & selection issues
- Improved validation & error messages
- Reduced redundant code without removing functionality
- Prepared structure for dark mode & future upgrades

## 🧩 Planned Improvements (Next Phase)
- 🌙 Dark Mode toggle
- 📊 Advanced dashboard charts
- 👥 Role-based access control
- 🔍 Search & filter improvements
- ⚙ Settings customization

## ✨ Credits

**Developed by:**

- **Ryan** – Core implementation, UI redesign, database logic, system flow
- **ChatGPT** – Architecture guidance, UI/UX concepts, refactoring, documentation
```
