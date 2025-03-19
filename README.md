# Payback Application - Recruitment Task

This is a console-based application built with Spring Boot to process requests asynchronously. The application accepts different types of requests, processes them, and reacts based on the request type. The types of requests are:

1. **TYPE1** – Save to database
2. **TYPE2** – Reject the request
3. **TYPE3** – Save to file
4. **TYPE4** – Log the request to the console

The requests are handled by a request processor that pulls requests from a queue, processes them based on their type, and performs the corresponding action. The application runs in a console environment and interacts with the user to input requests.

### HTTP Version
If HTTP requests were required, the same service could be used, but the user input would come via an HTTP POST request through a Spring Controller. In this case, the user would send requests to a REST API instead of entering them in the console.

## Requirements

- **JDK 17+**
- **Maven**
- **Spring Boot environment**

## Configuration

```properties
  # Database connection details and log file path
  spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
  spring.datasource.username=your_db_username
  spring.datasource.password=your_db_password
  spring.datasource.driver-class-name=org.postgresql.Driver
  
  request.logFilePath=./your_file_name
```
