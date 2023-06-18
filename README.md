# cinema-booking-app

Seat reservation system for a multiplex.

## Solution description

Solution consists of two separate projects: 
### - cinema-rest-api 
Api responsible for dealing with data from Postgres database.
Program exposes endpoints available from `localhost:8080/`
### - cinema-client
Client application that allows create reservation by the user.
Running the program start the reservation dialog with the user.

Note: In this project following logic was included:

- Seats can be booked at latest 15 minutes before the screening begins.
- name and surname should each be at least three characters long, starting
  with a capital letter. The surname could consist of two parts separated with a
  single dash, in this case the second part should also start with a capital letter.
- There cannot be a single place left over in a row between two already reserved
  places.

## Requirements
1) running docker to start Postgres image
2) Default java as Java 8
3) Sbt installed

## Run demo

### 1. Run backend REST api service

Open new terminal widow and run command below:

```
    ./start-rest-service.sh
```

Do not close terminal window.

### 2. Run DEMO scenario 

Open new terminal widow and run command below:

```
    ./demo.sh
```
Execution demo will be displayed in the terminal widow

## Run with custom input data

It is possible to test the solution with custom input. Do the following steps:

### 1. Run backend REST api service

Open new terminal widow and run command below:

```
    ./start-rest-service.sh
```

Do not close terminal window.

### 2. Run reservation dialog

Start client with the commands below:

```
    cd cinema-client
    sbt assembly
    java -jar target/scala-2.13/cinema-client-assembly-0.1.0-SNAPSHOT.jar
```

Interactively type answers to questions asked by dialogue in the console.
