| Assessment Details | Please Complete All Details                                             |
| ------------------ | ----------------------------------------------------------------------- |
| Group              | A                                                                       |
| Module Title       | IY4113 Applied Software Engineering using Object-Orientated Programming |
| Assessment Type    | Assigment                                                               |
| Module Tutor Name  | Jonathan Shore                                                          |
| Student ID Number  | T0466145                                                                |
| Date of Submission | 29/04/2026                                                              |
| Word Count         | 800                                                                     |

- [x] *I confirm that this assignment is my own work. Where I have referred to academic sources, I have provided in-text citations and included the sources in 
  the final reference list.*

- [x] *Where I have used AI, I have cited and referenced appropriately.

---

### Research (minimum of 2, at least 3)

---

Conduct research to support your coding process, including use of code examples, tutortials, documentation and AI tools (if used).
Use the structure below to capture your evidence:

---

*Title of research: *Object Oriented Programming in java
*Reference (link):[J*ava OOP(Object Oriented Programming) Concepts - GeeksforGeeks](https://www.geeksforgeeks.org/java/object-oriented-programming-oops-concept-in-java/)
*How does the research help with coding practise?:*

This research helped me better understand how object-oriented programming can organize a Java app into separate classes, each with its own job (GeeksforGeeks, 2024). The article explained key ideas like encapsulation, abstraction, inheritance, and polymorphism. It showed me why big programs should be split into smaller, reusable classes instead of putting everything into one file or method. This makes the code easier to read, maintain, debug, and grow over time.

For the CityRide Lite project, this research led to creating separate classes like:

- Journey

- FareCalculator

- JourneyManager

- InputValidator

- Menu

This setup follows good coding practices and makes the system easier to manage and expand (GeeksforGeeks, 2024).

*Key coding ideas you could reuse in your program:*

- Put different jobs in different classes

- Write methods you can use more than once

- Use private variables and getters to access them

- Break big tasks into smaller, simpler steps

- Keep methods short and easy to read

- Split code into separate parts so it's easier to fix and update

Screenshot of research:
![](Research images/Research1.png)

---

Title of research:Java Collections 
Reference (link):[Lesson: Custom Collection Implementations (The Java&trade; Tutorials &gt; Collections)](https://docs.oracle.com/javase/tutorial/collections/custom-implementations/index.html)
How does the research help with coding practise?:

The Oracle Collections Framework Documentation explains that HashMap is useful for storing data as key-value pairs (Oracle, 2025). It allows you to quickly find, add, or remove items using a unique key.Using a HashMap encourages good coding practices in several ways. First it makes the code more efficient because finding an item by its key is very fast, even when there are many items. Second it keeps the code clean and simple since you don't need to write extra loops to search for a specific journey. Third it reduces errors because each journey has a unique key, so you won't accidentally mix up different journeys. Finally it makes the code easier to read and understand because the key clearly identifies what each journey represents for example using a journey ID as the key.

This influenced the decision to store all journey objects inside aHashMap<String, Journey> within the JourneyManager class.

Key coding ideas you could reuse in your program:

- Added using a unique ID (like a journey number)

- Retrieved quickly by that ID

- Removed easily

- Checked for existence efficiently (Oracle, 2025)

*Screenshot of research:*
![](Research images/Research2.png)

---

### Program Code

---

Paste the current program code created so far. It does not have to be runnable code (document though if it does not work!)

*Program code goes here:*

### Updated Gantt Chart

---

![](Docs/CityRide_Gantt3.png)

### Diary Entries

---

### Entry 1 – Date []

# References

GeeksforGeeks (2024).*Object oriented programming in Java*. Available at:[Java OOP(Object Oriented Programming) Concepts - GeeksforGeeks](https://www.geeksforgeeks.org/object-oriented-programming-oops-concept-in-java/)(Accessed: 11 May 2026).

Oracle (2025).*The Collections Framework*. Available at:[Trail: Collections (The Java&trade; Tutorials)](https://docs.oracle.com/javase/tutorial/collections/index.html)(Accessed: 11 May 2026).