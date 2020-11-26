# POC: Spring Specification Filters

It demonstrates how to implement an abstraction that translates user input to Spring Specification.

The application should interpret the user input (from GET request to our API, for example) and extract a comparison criteria like equals, contains, etc, and to convert that to a Spring Specification.

Assuming that we have a SQL table named `fruits` with columns `name` and `color`, receiving `is:yellow,red,orange` should create a Specification that returns all fruits persisted that has the value yellow, red or orange in the `color` column.

## How to run

| Description | Command |
| :--- | :--- |
| Run tests | `./gradlew test` |
