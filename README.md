# Student Daily Planner

A Java-based application designed to help students manage their daily academic schedules, assignments, and reminders effectively.

## Features

- Student Login/Signup system with secure authentication
- Class Schedule Management with intuitive interface
- Assignment Tracking with due dates and priorities
- Exam Reminders with countdown functionality
- Personal Reminders for non-academic tasks
- Daily Calendar View with event visualization
- Easy-to-use interface for managing academic activities

## Technical Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Database 8.0 or higher
- MySQL JDBC Driver (included in project)
- JCalendar Library (included in project)

## Database Setup

1. Install MySQL if not already installed
2. Start MySQL service
3. Create a new database:
   ```sql
   mysql -u root
   CREATE DATABASE student_planner;
   ```
4. Run the database setup script:
   ```bash
   mysql -u root student_planner < database.sql
   ```

## Project Setup

1. Clone the repository
2. Ensure MySQL is running
3. Update database connection settings in `DBConnection.java` if needed:
   - URL: jdbc:mysql://localhost:3306/student_planner
   - Username: root
   - Password: (your MySQL password)
4. Run the application:
   ```bash
   ./run.sh
   ```
   Or compile and run manually:
   ```bash
   javac -cp ".:mysql-connector-j-8.0.33.jar:jcalendar-1.4.jar" *.java
   java -cp ".:mysql-connector-j-8.0.33.jar:jcalendar-1.4.jar" Main
   ```

## Project Structure

- `Main.java` - Application entry point and initialization
- `DBConnection.java` - Database connection management
- `Login.java` - Login and signup functionality
- `Dashboard.java` - Main dashboard interface
- `ScheduleManager.java` - Core schedule management functionality
- `ScheduleManagerController.java` - Controller for schedule operations
- `ScheduleManagerModel.java` - Data model for schedule management
- `ScheduleManagerView.java` - UI components for schedule management
- `database.sql` - Database schema and initial setup
- `run.sh` - Convenience script for running the application

## Usage Guide

1. **Login/Signup**
   - Launch the application
   - Enter credentials or create a new account

2. **Managing Schedule**
   - Add new classes with time slots
   - Set up recurring schedules
   - View daily/weekly schedule

3. **Assignment Management**
   - Add new assignments with due dates
   - Set priority levels
   - Track completion status

4. **Reminders**
   - Set exam reminders
   - Create personal reminders
   - Get notifications for upcoming events

## Dependencies

- MySQL Connector/J 8.0.33
- JCalendar 1.4

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.