# CityRide Lite

CityRide Lite is a Java command-line application for recording public transport journeys and tracking fares. Riders can manage a profile and journey history, while administrators can maintain the fare rules used by the application.

## Features

- Create or load a rider profile with a passenger type and preferred payment option.
- Add, edit, remove, list, and filter journeys across the supported travel zones.
- Calculate fares using configured routes, peak periods, passenger discounts, and daily spending caps.
- View daily summaries and totals grouped by passenger type.
- Import journey data from CSV and export journey reports as CSV or text.
- Save rider profiles and journey sessions locally for later use.
- Use the password-protected admin menu to manage fares, discounts, daily caps, and peak time windows.

## Requirements

- Java Development Kit (JDK) 11 or later.

The project has no external library or build-tool requirements.

## Build and run

From the project root, compile the source files and start the application:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

At startup, choose the rider or admin menu. The admin menu's default password is `admin`. The current implementation resets a changed admin password when the application is restarted.

## Data files

The application reads and writes data relative to the project root. It creates the relevant folders when needed.

- `profiles/`: saved rider profiles in JSON format.
- `sessions/`: saved rider journey sessions in JSON format.
- `imports/`: CSV files available for import.
- `exports/`: raw journey CSV exports.
- `reports/`: formatted CSV reports and text summaries.
- `config/config.json`: saved fare and fare-rule configuration. Defaults are used when this file is absent or unreadable.

## Project layout

- `src/`: Java application source code.
- `Part 1 Docs/` and `Part 2 Docs/`: project milestone documents.
- `Flowcharts/` and `class diagram/`: design diagrams.
- `Research images/`: images collected for project research.
- `profiles/` and `reports/`: example or previously generated application data.





