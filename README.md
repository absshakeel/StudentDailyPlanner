# Student Daily Planner

A Java-based desktop application to help university students manage their daily academic activities including classes, assignments, exams, and personal reminders.

## Features

- Student Login/Signup system
- Class Schedule Management
- Assignment Tracking
- Exam Reminders
- Personal Reminders
- Daily Calendar View

## Technical Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Database
- MySQL JDBC Driver

## Database Setup

1. Install MySQL if not already installed
2. Create a new database:
   ```sql
   mysql -u root
   CREATE DATABASE student_planner;
   ```
3. Run the database setup script:
   ```bash
   mysql -u root student_planner < database.sql
   ```

## Project Setup

1. Clone the repository
2. Ensure MySQL is running
3. Update database connection settings in `DBConnection.java` if needed:
   - URL: jdbc:mysql://localhost:3306/student_planner
   - Username: root
   - Password: 

## Compilation and Running

```bash
# Compile the Java files
javac -cp mysql-connector-j-8.0.33.jar *.java

# Run the application
java -cp .:mysql-connector-j-8.0.33.jar Main
```

## Project Structure

- `Main.java` - Application entry point
- `DBConnection.java` - Database connection management
- `Login.java` - Login and signup functionality
- `Dashboard.java` - Main dashboard interface
- `ScheduleManager.java` - Schedule management functionality
- `database.sql` - Database schema and setup script

## Database Schema

### Users Table
- id (Primary Key)
- name
- username (Unique)
- password

### Classes Table
- id (Primary Key)
- user_id (Foreign Key)
- title
- day
- time
- location

### Assignments Table
- id (Primary Key)
- user_id (Foreign Key)
- title
- subject
- due_date
- status

### Exams Table
- id (Primary Key)
- user_id (Foreign Key)
- subject
- exam_date
- time

### Reminders Table
- id (Primary Key)
- user_id (Foreign Key)
- title
- date
- time
- repeat