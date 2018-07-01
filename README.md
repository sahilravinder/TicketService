# TicketService
Wal-Mart Labs coding challenge

Implementation of a Simple Ticket Service that facilitates the discovery, temporary hold and final reservation of seats within a high-demand performance venue

The functions provided by the service are as follows - 

1. Find the number of available seats within the venue.
2. Find and hold the best available seats on behalf of a customer.
3. Reserve and commit a specific group of held seats for a customer.

### Assumptions made 
---
1. Venue consists of rows, each row having n seats.
2. Seats are assigned in the rows based on availability with best seats facing the stage.
---

### Configuration
---
The venue row and seat limit is configurable, so is the hold duration. For the purpose of this demo, pre-configured values are as follows 
1. Venue Rows - 5
2. Venue Seats - 20 per Row
3. Hold Duration - 50 seconds
---

### Components
---
1. Java Version - 1.8
2. Testing - Junit
3. Logging - log4j
---


