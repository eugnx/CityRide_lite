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

![](Part 2 Docs/Flowch1.jpg)
![](Part 2 Docs/Flowch 2.jpg)
![](Part 2 Docs/Flowch3.jpg)
![](Part 2 Docs/Flowcha1.jpg)
![](Part 2 Docs/Flowcha2.jpg)
![](Part 2 Docs/Flowcha3.jpg)
![](Part 2 Docs/Flowcha4.jpg)
![](Part 2 Docs/Flowcha5.jpg)
![](Part 2 Docs/Flowcha6.jpg)
![](Part 2 Docs/Flowcha7.jpg)

- *Include a class diagram to demonstrate the class structure of the proposed program design.
  

------------------------------------------------------------------------------------------------------------------------------

### Research (minimum of 1 required, preferrebly 2)

---

*Research existing programs that solve a similar problem. The program does not have to be written in java or object orientated in nature - just solve a similar type of problem.*
*Use the strucutre below to capture your evidence:*
------------------------------------------------------------------------------------------------------------------------------


### Name of program:

Transport for London (TfL) Oyster and Contactless

### Reference (link):

[Transport for London](https://tfl.gov.uk/fares/contactless-and-oyster-account)

### What it does well (2-3 features that work effectively):

1. Automatically calculates journey fares based on travel zones.
2. Applies daily fare caps automatically.
3. Stores journey history for later review.

These features support efficient fare management and improve usability for regular passengers. Modern transport systems benefit from automated fare calculation because it reduces user error and improves system reliability (Vuchic, 2005).

### What it does poorly (at least 1 feature):

1. Some fare information is spread across multiple pages, making navigation difficult for new users.

### Key design ideas you could reuse:

- Automatic fare calculation.
- Daily cap tracking.
- Journey history records.
- Summary reporting at the end of the day.

##Screenshot (showing the interface/output):
![](Part 2 Docs/Research 1.png)


### Name of program: 
OMNY

### Reference (link):

[https://omny.info](https://omny.info)

### What it does well (2-3 features that work effectively):

1. Provides contactless payment support.
2. Stores user travel history.
3. Offers account-based journey tracking.

The account-based approach provides persistence and allows users to access previous travel records. Persistent data storage is an important aspect of modern information systems (Elmasri and Navathe, 2016).

### What it does poorly (at least 1 feature):

1. Limited ability to modify recorded journeys after they have been completed.

### Key design ideas you could reuse:

- User profile management.
- Secure account access.
- Stored journey history.
- Clear menu-based navigation.

### Screenshot (showing the interface/output):

![](Part 2 Docs/research 2.png)

### Design justification:

The profile management functionality is directly relevant to the CityRide Lite requirement for creating, saving and loading rider profiles. Separating user information from journey records also follows good software design principles (Larman, 2004).



# Program Review 3 – Oyster Fare Calculator

### Name of program:

Oyster Fare Calculator

### Reference (link):

https://oysterfares.com/information-pages/fares-guide-tables/

### What it does well (2-3 features that work effectively):

1. Clearly displays fare calculations.
2. Separates peak and off-peak pricing.
3. Uses a simple interface for selecting zones.

### What it does poorly (at least 1 feature):

1. Does not provide profile management or journey storage.

### Key design ideas you could reuse:

- Zone-based fare calculation.
- Peak/off-peak fare selection.
- Clear display of fare results.
- Simple input structure.

### Screenshot (showing the interface/output):

![](Part 2 Docs/research3(1).png)
![](Part 2 Docs/research3(2).png)
![](Part 2 Docs/research3(3).png)

### Design justification:

The simple fare calculation process provides a useful model for the CityRide Lite fare engine. The use of separate peak and off-peak rates aligns with the project specification and demonstrates clear functional decomposition (Bloch, 2018).

### Gantt Chart

------------------------------------------------------------------------------------------------------------------------------

![](Part 2 Docs/CityRide_Part2_Gantt.png)
------------------------------------------------------------------------------------------------------------------------------

### Diary Entries

------------------------------------------------------------------------------------------------------------------------------

9/06/2026 -Diary Entry 1 – research on CSV and JSON files
Today I researched JSON and CSV handling in Java, looking at Gson for JSON and OpenCSV for CSV files, while also investigating how real transport fare systems work to better understand daily caps. I did this because research is important before designing: I need to know what libraries are available and how they work, as that will affect my class design for instance, using Gson means my classes need to be serializable. I encountered a few problems along the way: there are multiple JSON libraries, with Jackson being more powerful but harder to learn, whereas Gson is simpler, so I decided to use Gson for this project. For CSV, OpenCSV is good, but adding a library might complicate submission, so I may implement simple CSV parsing myself instead.

10/06/2026 -Diary Entry 2 – flowchart design
Today I created all the flowcharts for the main functions, including role selection, profile management, journey operations, import/export, admin functions, and save on exit. I did this because flowcharts help me visualize the program logic before coding, making it easier to spot problems or missing steps, and when I start coding I can follow them step by step. The main problem I encountered was that creating flowcharts for all functions took a long time some functions like edit journey have many steps, so I had to break them down into smaller pieces to keep the flowchart readable.

11/06/2026 -Diary Entry 2 – Class diagram
Today I created the class diagram showing all classes and their relationships, identifying the main classes as AppController, RiderProfile, Journey, Config, FileHandler, FareCalculator, InputValidator, Summary, and the enums. I did this because the class diagram is the blueprint for my code it shows which classes I need to create and how they connect, which will save time when I start coding since I already know the structure. The problem I encountered was that deciding which class should be responsible for which function was challenging; for example, I had to decide whether FareCalculator should be a separate class or part of Journey, and I eventually kept it separate to follow the Single Responsibility Principle.

------------------------------------------------------------------------------------------------------------------------------

### References

Bloch, J. (2018). *Effective Java*. 3rd ed. Boston: Addison-Wesley.

Elmasri, R. and Navathe, S.B. (2016). *Fundamentals of Database Systems*. 7th ed. Harlow: Pearson.

Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Boston: Addison-Wesley.

Larman, C. (2004). *Applying UML and Patterns*. 3rd ed. Upper Saddle River: Pearson.

McConnell, S. (2004). *Code Complete*. 2nd ed. Redmond: Microsoft Press.

Sommerville, I. (2020). *Software Engineering*. 10th ed. Harlow: Pearson.

Transport for London. (2026). *Contactless and Oyster Account*. Retrieved from: [Transport for London](https://tfl.gov.uk/fares/contactless-and-oyster-account) [Accessed 12 June 2026].

OMNY. (2026). *OMNY Official Website*. Retrieved from: [https://omny.info](https://omny.info) [Accessed 12 June 2026].

Oyster Fares. (2026). *Fare Guide Tables*. Retrieved from: https://oysterfares.com/information-pages/fares-guide-tables/ [Accessed 12 June 2026].

Vuchic, V.R. (2005). *Urban Transit: Operations, Planning and Economics*. Hoboken: Wiley.
