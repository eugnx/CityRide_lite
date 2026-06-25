# IY4113 Part 2 Milestone 3

| Assessment Details | Please Complete All Details                                      |
| ------------------ | ---------------------------------------------------------------- |
| Group              | A                                                                |
| Module Title       | Applied Software Engineering using Object-Orientated Programming |
| Assessment Type    | coursework                                                       |
| Module Tutor Name  | Jonathan Shore                                                   |
| Student ID Number  | P466145                                                          |
| Date of Submission | 28/06/2026                                                       |
| Word Count         |                                                                  |
| GitHub Link        |                                                                  |

- [x] *I confirm that this assignment is my own work. Where I have referred to academic sources, I have provided in-text citations and included the sources in
  the final reference list.*
- [x] *Where I have used AI, I have cited and referenced appropriately.

------------------------------------------------------------------------------------------------------------------------------

### Research (minimum of 2, at least 3)

---

Conduct research to support your coding process, including use of code examples, tutortials, documentation and AI tools (if used).
Use the structure below to capture your evidence:

------------------------------------------------------------------------------------------------------------------------------

### Research 1 – JSON File Handling in Java

**Title of research:** Java JSON file handling without external libraries

**Reference (link):** Oracle (2025). *Java I/O Streams*. Available at: [Lesson: Basic I/O (The Java&trade; Tutorials &gt; Essential Java Classes)](https://docs.oracle.com/javase/tutorial/essential/io/) [Accessed 20 June 2026].

**How does the research help with coding practice?**

This research helped me understand how to read and write structured data to files in Java without relying on external libraries like Gson or Jackson. Since adding third-party dependencies can complicate project submission, I decided to implement a hand-written JSON serialiser using Java's built-in tools. The Oracle documentation explains that the `java.nio.file.Files` class provides simple, single-call methods such as `Files.readString()` and `Files.writeString()` for reading and writing entire text files, which is well suited to small structured files like rider profiles and system configuration (Oracle, 2025a). Understanding this allowed me to build the `ProfileFileHandler` and `ConfigManager` classes, which both convert Java objects to JSON-formatted strings using `StringBuilder` and parse them back without any third-party dependency.

Good software design separates responsibilities into distinct classes, so that each class has one clearly defined job (McConnell, 2004). Following this principle, I placed all file-writing logic in dedicated handler classes rather than mixing it into the menu or business-logic classes. This made the code easier to test and maintain, because changing the file format only requires updating the handler class and nothing else.

The main challenge was parsing JSON back into Java objects without a library. I solved this by writing a helper method called `jsonString()` inside `JourneyFileHandler` that scans the string for a named key and extracts the value that follows it. This is a practical example of what Sommerville (2020) calls defensive programming — designing code to handle unexpected or missing data gracefully rather than crashing.

**Key coding ideas reused in the program:**

- Using `Files.writeString()` and `Files.readString()` for simple file I/O (Oracle, 2025a)
- Using `StringBuilder` to build structured JSON output one field at a time
- Writing defensive parsing that catches exceptions and falls back to defaults rather than crashing (Sommerville, 2020)
- Separating file I/O responsibility into dedicated handler classes following the Single Responsibility Principle (McConnell, 2004)

Screenshot of research:
![](Part 2 Docs/Research 2(1).png)

------------------------------------------------------------------------------------------------------------------------------

### Research 2 – CSV File Handling in Java

**Title of research:** Reading and writing CSV files in Java using BufferedReader and BufferedWriter

**Reference (link):** Oracle (2025b). *BufferedReader (Java SE 21)*. Available at: https://docs.oracle.com/en/java/docs/api/java.base/java/io/BufferedReader.html [Accessed 21 June 2026].

**How does the research help with coding practice?**

This research helped me understand how to read and write CSV files line by line in Java using `BufferedReader` and `BufferedWriter`, which are part of the standard `java.io` package. The Oracle documentation explains that `BufferedReader` reads text efficiently by buffering characters internally, which reduces the number of actual read operations on the underlying file and improves performance when processing files line by line (Oracle, 2025b). This is the standard approach for processing text files in Java and is more efficient than reading the file character by character.

For the `JourneyFileHandler` class, I used `BufferedReader` wrapped around a `FileReader` to import journeys from CSV files, and `BufferedWriter` wrapped around a `FileWriter` to export them. Each line is split using `String.split(",", -1)` — the `-1` limit argument is important because without it Java drops trailing empty fields, which could cause incorrect column alignment when optional fields such as journey time are blank. This approach to handling optional fields follows the principle of writing robust code that does not make assumptions about the completeness of input data (Bloch, 2018).

One problem I encountered was making the CSV importer backwards-compatible with the old file format from Part 1, which had 10 columns and no time field. My updated format has 11 columns. I solved this by checking whether the third column matches the pattern `HH:MM` — if it does, the parser treats it as a time value; if not, it assumes the old format and skips the time field. Bloch (2018) recommends writing APIs that are easy to use correctly and hard to use incorrectly, and this approach applies the same idea to file parsing: the importer accepts both formats without requiring the user to specify which one they are providing.

**Key coding ideas reused in the program:**

- Using `BufferedReader` and `BufferedWriter` for efficient line-by-line file I/O (Oracle, 2025b)
- Using `String.split(",", -1)` with a negative limit to preserve trailing empty fields
- Writing backwards-compatible parsing that handles both old and new CSV formats (Bloch, 2018)
- Handling `IOException` with try-catch and printing clear error messages rather than letting the program crash (Sommerville, 2020)

**Screenshot of research:**
![](Part 2 Docs/Research 2(2).png)

### Research 3 – Singleton Design Pattern in Java

**Title of research:** Singleton pattern for shared configuration objects in Java

**Reference (link):** [Books/Erich Gamma, Richard Helm, Ralph Johnson, John M. Vlissides-Design Patterns_ Elements of Reusable Object-Oriented Software -Addison-Wesley Professional (1994).pdf at master · ansbilalgit/Books · GitHub](https://github.com/ansbilalgit/Books/blob/master/Erich%20Gamma%2C%20Richard%20Helm%2C%20Ralph%20Johnson%2C%20John%20M.%20Vlissides-Design%20Patterns_%20Elements%20of%20Reusable%20Object-Oriented%20Software%20%20-Addison-Wesley%20Professional%20(1994).pdf) Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Boston: Addison-Wesley.

**How does the research help with coding practice?**

The specification requires configuration to be loaded from a file at startup and made available to every part of the program for the rest of its lifetime. This is a classic use case for the Singleton design pattern. Gamma et al. (1994) define the Singleton as a pattern that ensures a class has only one instance and provides a global point of access to it. The pattern is especially useful for shared resources such as configuration objects, connection pools, and logging systems, where having more than one instance would cause inconsistency.

I used this pattern to implement `ConfigManager`. The class has a private constructor to prevent external instantiation and a public static method `getInstance()` that creates the single instance the first time it is called, then returns the same instance on every subsequent call. This means the configuration is loaded once at startup — when `Main` calls `ConfigManager.getInstance()` — and all other classes including `FareCalculator`, `Journey`, and `AdminMenuHandler` access the same live configuration without needing it to be passed as a parameter between methods (Gamma et al., 1994).

Without this pattern, I would have needed to create a `ConfigManager` object in `Main` and pass it through constructors to every class that needed it. This approach, sometimes called constructor injection, can make the code harder to read when many classes depend on the same object. The Singleton approach keeps the design clean and directly supports the Single Responsibility Principle, since `ConfigManager` manages configuration and nothing else (McConnell, 2004). This also means that when an admin saves a new fare value, all other parts of the program pick up the change immediately, because they all reference the same singleton instance.

**Key coding ideas reused in the program:**

- Private constructor in `ConfigManager` to prevent direct instantiation (Gamma et al., 1994)
- Static `instance` field and `getInstance()` method to provide global, controlled access
- Calling `ConfigManager.getInstance()` in `Main` to trigger file loading before the menus start
- Keeping `ConfigManager` focused on one job — managing configuration — following the Single Responsibility Principle (McConnell, 2004)

**Screenshot of research:**
![](Part 2 Docs/Research 2(3).png)

------------------------------------------------------------------------------------------------------------------------------

### Program Code

---

Paste the current program code created so far. It does not have to be runnable code (document though if it does not work!)
------------------------------------------------------------------------------------------------------------------------------

*Program code goes here:*
------------------------------------------------------------------------------------------------------------------------------

### Updated Gantt Chart

------------------------------------------------------------------------------------------------------------------------------

![](Part 2 Docs/CityRide_Part2_Gantt.png)
------------------------------------------------------------------------------------------------------------------------------

### Diary Entries (at least 4)

------------------------------------------------------------------------------------------------------------------------------

### Entry 1 – Date [19/06/2026] – Starting the Part 2 implementation

Today I started writing the Java code for Part 2. I began with the new classes that Part 1 did not have: `ConfigManager`, `RiderProfile`, `ProfileFileHandler`, and `PaymentOption`. I started with these because the rest of the program depends on them — the rider profile is needed before journeys can be added, and the config must load before any fares can be calculated. This follows the approach recommended by McConnell (2004), who suggests building foundational classes before the classes that depend on them, to reduce the number of times code needs to be revised.

The main problem I encountered was deciding how to store the configuration. At first I thought of using a simple `.properties` file, but the config includes nested data like fare tables with multiple keys (zone pair and time band). A JSON format handles these nested structures more clearly. Since I decided not to use external libraries, I had to write the JSON serialiser myself using `StringBuilder` and the `Files.writeString()` method from the `java.nio.file` package (Oracle, 2025a). This took longer than expected but gave me a much better understanding of how string building and file writing work in Java.



### Entry 2 – Date [21/06/2026] – Implementing file I/O for profiles and sessions

Today I implemented `ProfileFileHandler` and the session save/load functionality in `JourneyFileHandler`. The profile handler reads and writes a single-object JSON file. The session handler writes an array of journey objects so the rider can restore their previous journeys the next time they log in. Storing data so that it survives between program runs is a key feature of real-world applications, and the Oracle documentation on `java.io` streams provided the guidance I needed to implement this correctly (Oracle, 2025b).

The biggest problem was parsing JSON back into Java objects without a library. My first approach tried to use `split()` on the JSON string, but this broke when field values contained commas. I solved this by writing a helper method called `jsonString()` that scans character by character for a named key and extracts the value that follows it. This approach is more reliable because it reads the actual structure of the JSON rather than just splitting on commas. Sommerville (2020) notes that defensive programming means anticipating likely failure points and handling them explicitly, and avoiding `split()` on structured data is exactly this kind of defensive decision.

I also had to handle backwards compatibility with the old CSV format from Part 1, which had no time column. I added a check in the CSV importer that detects whether the third column looks like a time value (`HH:MM`) and adjusts the parsing accordingly, meaning old CSV files can still be imported without errors (Bloch, 2018).



### Entry 3 – Date [23/06/2026] – Admin menu and ConfigManager persistence

Today I rewrote `AdminMenuHandler` to include full add, update, and delete operations for base fares, passenger discounts, daily caps, and peak time windows. I also made these changes actually save to `config/config.json` so they persist between sessions. In the original code, the admin could validate a new value but the change was never written to any file, making the admin menu essentially read-only.

The main problem I encountered was making `ConfigManager` accessible from `AdminMenuHandler`, `FareCalculator`, and `Journey` all at the same time without passing it as a constructor parameter to every class. I solved this using the Singleton design pattern as described by Gamma et al. (1994). `ConfigManager.getInstance()` always returns the same object, so any class can call it and get the current live configuration. When the admin saves a change, all other parts of the program pick it up immediately, because they all reference the same singleton instance.

I also added a peak time window management menu, which was completely missing from the original submission. The admin can now set the start and end hour for both the morning and evening peak windows, and these are validated to ensure the end hour is always greater than the start hour before any change is saved. Bloch (2018) recommends validating all parameters at the entry point of a method and failing fast with a clear error message if they are invalid — this is exactly the pattern I followed.



### Entry 4 – Date [20/06/2026] – Testing and bug fixing

Today I tested the complete program from start to finish, testing the rider role by creating a profile, adding journeys across different zones and time bands, verifying that the daily cap was applied correctly, exporting a report, and then exiting and restoring the session on the next run. I also tested the admin role by logging in, updating a base fare, and logging back in as a rider to confirm the new fare was used in calculations. McConnell (2004) recommends testing both normal cases and boundary conditions, so I tested cap behaviour at exactly the cap limit as well as just above it.

I found and fixed two bugs during testing. The first was that `offerSaveOnExit()` was calling `scanner.close()`, which caused a `NoSuchElementException` when the program tried to read a Y/N response immediately after. The correct pattern is for the class that creates a resource to be responsible for closing it (Oracle, 2025a); since `Main` creates the `Scanner`, I moved `scanner.close()` there instead.

The second bug was that when a rider loaded a profile and chose not to restore their previous session, the journeys were still being added to the list before the Y/N prompt was shown. I fixed this by restructuring `offerSessionRestore()` so that journeys are only added after the user explicitly confirms with Y. After both fixes the program compiled cleanly and all manual tests passed.

------------------------------------------------------------------------------------------------------------------------------

## References

Bloch, J. (2018). *Effective Java*. 3rd ed. Boston: Addison-Wesley.

Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Boston: Addison-Wesley.

McConnell, S. (2004). *Code Complete: A Practical Handbook of Software Construction*. 2nd ed. Redmond: Microsoft Press.

Oracle (2025a). *Java I/O Streams*. Available at: [Lesson: Basic I/O (The Java&trade; Tutorials &gt; Essential Java Classes)](https://docs.oracle.com/javase/tutorial/essential/io/) [Accessed 20 June 2026].

Oracle (2025b). *BufferedReader (Java SE 21)*. Available at: https://docs.oracle.com/en/java/docs/api/java.base/java/io/BufferedReader.html [Accessed 21 June 2026].

Oracle (2025c). *Files (Java SE 21)*. Available at: https://docs.oracle.com/en/java/docs/api/java.base/java/nio/file/Files.html [Accessed 20 June 2026].

Sommerville, I. (2020). *Software Engineering*. 10th ed. Harlow: Pearson.
