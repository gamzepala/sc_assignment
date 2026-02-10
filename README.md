# SC Assignment - Basic Java Project

This is a basic Java project template.

## Project Structure

```
sc_assignment/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── Main.java
│   │       └── HelloWorld.java
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## Build and Run

### With Maven:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

### With javac directly:
```bash
javac src/main/java/*.java -d out
java -cp out Main
```

## Requirements

- Java 11 or later
- Maven 3.6.0+ (optional)
