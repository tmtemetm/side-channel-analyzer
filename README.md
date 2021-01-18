# side-channel-analyzer

Side Channel Trace Analyzer Tool


## Description

Side Channel Analyzer is a desktop application for analyzing side channel traces of encryption algorithms. It is
specifically designed for performing Differential Power Analysis on AES power traces. Other features may be added in the
future.


## Project Info

- Issue tracking: https://github.com/tmtemetm/side-channel-analyzer/issues
- External Dependencies:
    - Java 11 or higher


## Local Development Setup

- Install OpenJDK 11 or higher
- Clone this Git repository
```
git clone git@github.com:tmtemetm/side-channel-analyzer.git
```
- Build the package using either the provided maven wrappers or with a local maven installation:

On Unix:
```
./mvnw clean package
```
Or on Windows:
```
mvnw.cmd clean package
```
- Run the application with
```
./mvnw spring-boot:run
```
or, if you prefer,
```
java -jar target/side-channel-analyzer-0.0.1-SNAPSHOT.jar
```
