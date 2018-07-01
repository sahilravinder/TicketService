# TicketService
Wal-Mart Labs coding challenge

Implementation of a Simple Ticket Service that facilitates the discovery, temporary hold and final reservation of seats within a high-demand performance venue

The functions provided by the service are as follows - 

1. Find the number of available seats within the venue.
2. Find and hold the best available seats on behalf of a customer.
3. Reserve and commit a specific group of held seats for a customer.

## Assumptions made 
---
* Seat Hold will expire in 10 seconds and held seats will be released.
* There are 5 Rows, each containing 20 Seats. 
---

## Implementation Notes

* The data model consists of - 
  1. SeatMap - The Venue with an array of Seat object (resembling a venue with NxN seating arrangement)
  2. Seat - Seat attributes namely RowNum, SeatNum and SeatStatus 
  3. SeatHold - Seat Hold info namely SeatHoldId, List<Seat>, CustomerEmail and HeldTime. 
  4. SeatStatus(enum) - Applicable statuses of AVAILABLE, HOLD or RESERVED.
  
* Ticket service implementation accepts a pre-defined SeatMap and holdDuration value configurable during initialization(see impl. below). The service also initiates a scheduled executor job to release tickets that have been held for more than defined holdDuration. 
    
## Service Initialization    

```java
 //Assign a seat layout
 SeatMap seatMap = new SeatMap(20, 5); //Initialize with 5 rows, each having 20 seats
  
  //Assign hold duration(seconds) for TicketService
 int holdDuration = 5;
 
 TicketService ticketService = new TicketVendor(seatMap, holdDuration);
```

## Testing and Building

* Test with:
``` shellsession
$ ./gradlew test
```

* Build a JAR with
``` shellsession
$ ./gradlew jar
...
$ ls -1 build/libs
TicketService-1.0-SNAPSHOT.jar
```

Lack of License
==
This software is not licensed. Do not distribute.
