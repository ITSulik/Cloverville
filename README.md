# Cloverville Community Manager

**Cloverville Community Manager** is a Java-based desktop application developed as part of a semester project at VIA University. The application helps the admins of the Cloverville ecological community manage members, track activities, assign points, and monitor trade and communal tasks. A complementary **website** provides residents with a view of green initiatives and trade offers.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Installation](#installation)
4. [User Guide](#user-guide)
5. [Project Structure](#project-structure)
6. [Technologies Used](#technologies-used)
7. [Team Roles](#team-roles)
8. [License](#license)

---

## Project Overview

Cloverville Community Manager was designed to help manage the “invisible work” in the Cloverville ecological village, including:

* Tracking **green actions** and communal tasks
* Managing **personal and community points**
* Recording **trade tasks and goods exchanges**
* Displaying reports of completed tasks and point allocations
* Allowing only admins to add, edit, and manage activities

The application aims to simplify task tracking, encourage sustainability, and provide inspiration for the community through a points-based system.

---

## Features

* **Member Management:** Add, edit, delete, and search members
* **Activity Management:** Add, edit, complete, and search activities
* **Activity Types:** Green, Communal, Trade Task, Trade Goods
* **Points System:**

  * Green points for eco-friendly actions (community pool)
  * Personal points for communal tasks and trade activities
  * Weekly bonus points automatically applied based on participation
* **History Log:** All activities and point changes recorded in `History.txt`
* **Filter and Search:** Quickly find activities or members based on criteria
* **Admin-only Access:** Only designated admins (Green Bob and Don) can modify data

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/ITSulik/Cloverville
```

2. Open the project in **IntelliJ IDEA** (or any preferred Java IDE).
3. Make sure you have **Java 17** (or compatible) installed.
4. Run the `Main` to launch the application.

**Note:** The `History.txt` file and `UserGuide.pdf` are located in the root folder. Ensure the app has read/write permissions for the root folder to store history logs.

---

## User Guide

A complete **User Guide** is included in the repository (`UserGuide.pdf`). It covers:

* Access and login
* Dashboard overview
* Managing members (CRUD, search)
* Managing activities (CRUD, completion, search, and filter)
* User account management
* Community settings
* Explanation of the points system and history log

---

## Project Structure

```
/ClovervilleProject
│
├── /src
│   ├── /bob/cloverville
│   │   ├── /Controllers
│   │   ├── MemberService.java
│   │   ├── ActivityService.java
│   │   ├── Main.java
│   │   └── ... other Java classes
│   ├── /resources
│   │   ├── memberCreate.fxml
│   │   ├── activityCreate.fxml
│   │   └── ... other FXML files
│
├── History.txt
├── UserGuide.pdf
├── README.md
└── ... other files
```

---

## Technologies Used

* Java 17
* JavaFX (UI)
* FXML for layouts
* Collections API (Lists, Maps)
* File I/O for history logs
* Maven/Gradle for project management (if used)

---

## Team Roles

* **Java Application Development:** Medvetchi Valentin
* **Website Development:** Vladyslav Averin
* **Documentation and Testing:** Mateo Peche & Javier Vincenti

Collaboration was coordinated using version control, with weekly meetings and shared documentation to ensure alignment.

---

## License

This project is for **educational purposes** only. Do not redistribute without permission.
