# IY4113 Milestone 1 Part 2

| Assessment Details | Please Complete All Details                                      |
| ------------------ | ---------------------------------------------------------------- |
| Group              | A                                                                |
| Module Title       | Applied Software Engineering using Object-Orientated Programming |
| Assessment Type    | coursework                                                       |
| Module Tutor Name  | Jonathan Shore                                                   |
| Student ID Number  | P4661445                                                         |
| Date of Submission |                                                                  |
| Word Count         |                                                                  |
| GitHub Link        |                                                                  |

- [x] *I confirm that this assignment is my own work. Where I have referred to academic sources, I have provided in-text citations and included the sources in
  the final reference list.*
- [x] *Where I have used AI, I have cited and referenced appropriately.

------------------------------------------------------------------------------------------------------------------------------

### Purpose of the Program

------------------------------------------------------------------------------------------------------------------------------

CityRide Lite Part 2 is an extended version of the journey tracking application from Part 1. The program helps users track their public transport journeys and manage their daily travel costs.

The main purpose of this program is to allow two different types of users. Riders can create personal profiles, add journeys throughout the day, edit or delete journeys if they make mistakes, and save their data to files so they can continue later. Riders can also import journeys from CSV files and export their journey data and reports. The program calculates fares based on zones crossed, applies passenger discounts, and enforces daily spending caps.

Admins have a separate password-protected menu where they can update the system configuration. This includes changing base fares, passenger discounts, daily caps, and peak time windows. All changes are validated before being saved to ensure the system stays consistent.

The program saves all data to files. Rider profiles are saved as JSON files. Journeys can be imported and exported as CSV files. Reports are saved as both CSV and readable text files. When the program starts, it loads configuration from a file. If the file is missing, it uses safe default values.

## 2. Core Functionality

### Rider Functions:

| Number | Function                        |
| ------ | ------------------------------- |
| 1      | Create a new rider profile      |
| 2      | Load an existing rider profile  |
| 3      | Save rider profile to JSON file |
| 4      | Add a new journey               |
| 5      | Edit an existing journey        |
| 6      | Delete a journey                |
| 7      | Import journeys from CSV file   |
| 8      | Export journeys to CSV file     |
| 9      | View all journeys               |
| 10     | View per-journey costs          |
| 11     | View running totals             |
| 12     | View end-of-day summary         |
| 13     | Export summary as CSV           |
| 14     | Export summary as text file     |
| 15     | Save current day state on exit  |

### Admin Functions:

| Number | Function                   |
| ------ | -------------------------- |
| 16     | Login with password        |
| 17     | View current configuration |
| 18     | Update base fares          |
| 19     | Update passenger discounts |
| 20     | Update daily caps          |
| 21     | Update peak time windows   |

### System Functions:

| Number | Function                                 |
| ------ | ---------------------------------------- |
| 22     | Load configuration from file on startup  |
| 23     | Use safe defaults if config file missing |
| 24     | Validate all user inputs                 |
| 25     | Show clear error messages                |
| 26     | Menu-driven interface with examples      |

# System Constraints

The system must follow several constraints:

- Only valid passenger types can be used.
- Journey IDs must be unique.
- Invalid IDs must be rejected.
- Fare values must be validated before saving.
- Configuration data must be loaded at startup.
- Safe default values must be used if configuration files are missing.
- Admin access must be password protected.
- Data must be stored using JSON and CSV formats.
- User input must be validated with clear error messages

### Input Process Output Table

------------------------------------------------------------------------------------------------------------------------------

![](Part 2 Docs/IPO part 2.png)

### Algorithm Design

---

- *Add **images** for the design of your algorithm. Choose either Flowchart or JSP diagrams to demonstrate the functional elements of the algorithm. There should be multiple images for this part as you are decomposing the problem into smaller elements.*
- *Include a class diagram to demonstrate the class structure of the proposed program design.

------------------------------------------------------------------------------------------------------------------------------

### Research (minimum of 1 required, preferrebly 2)

---

*Research existing programs that solve a similar problem. The program does not have to be written in java or object orientated in nature - just solve a similar type of problem.*
*Use the strucutre below to capture your evidence:*
------------------------------------------------------------------------------------------------------------------------------Name of program:
Reference (link):
What it does well (2-3 features that work effectively):
What it does poorly (at least 1 feature):
Key design ideas you could reuse (e.g., layout, navigation, input/output, program structure):
Screenshot (showing the interface/output):

------------------------------------------------------------------------------------------------------------------------------

### Gantt Chart

------------------------------------------------------------------------------------------------------------------------------

![](Part 2 Docs/CityRide_Part2_Gantt.png)
------------------------------------------------------------------------------------------------------------------------------

### Diary Entries

------------------------------------------------------------------------------------------------------------------------------

*Add diary entries here detailing what you have done, wny you have done it, and any problems encountered.*
------------------------------------------------------------------------------------------------------------------------------
