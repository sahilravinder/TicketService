package org.walmart.service;

import org.apache.log4j.Logger;
import org.walmart.jobs.TicketExpiration;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;
import org.walmart.model.SeatStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketVendor implements TicketService  {
    private final static Logger logger = Logger.getLogger(TicketVendor.class);

    private final SeatMap seatMap;
    private final int holdDuration;
    private ScheduledExecutorService scheduledExecutorService;
    private final Map<Integer, SeatHold> seatsHeld = new ConcurrentHashMap<>();
    private static final AtomicInteger seatHoldId = new AtomicInteger(0);

    public TicketVendor(SeatMap seatMap, int holdDuration) {
        this.seatMap = seatMap;
        this.holdDuration = holdDuration;
        this.startTicketExpirationJob();
    }

    private void startTicketExpirationJob() {
        scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(new TicketExpiration(this.seatsHeld, holdDuration),
                0,
                1,
                TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                    logger.info("Timed out waiting for Ticket Expiration task to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }

    @Override
    public int numSeatsAvailable() {
        return this.seatMap.getAvailableSeats();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        SeatHold seatHold = new SeatHold();
        seatHold.setCustomerEmail(customerEmail);

        if (this.seatMap.getAvailableSeats() >= numSeats) {
            List<Seat> seatsBooked = new ArrayList<>();

            List<Seat> availableSeats = seatMap.getSeatsByStatus(SeatStatus.AVAILABLE);
            for (Seat seat : availableSeats) {
                if (seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                    seatsBooked.add(seat);
                    seat.setSeatStatus(SeatStatus.HOLD);
                    numSeats--;
                }
                if (numSeats == 0)
                    break;
            }

            seatHold.setHeldTime(System.currentTimeMillis());
            seatHold.setHeldSeats(seatsBooked);
            seatHold.setSeatHoldId(seatHoldId.incrementAndGet());
            seatsHeld.put(seatHold.getSeatHoldId(), seatHold);
        }
        return seatHold;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        logger.info(String.format("Requested to RESERVE Seats with [ID:%d] by [%s]", seatHoldId, customerEmail));
        String confirmationMessage;
        if (this.seatsHeld.containsKey(seatHoldId)) {
            SeatHold seatHold = this.seatsHeld.get(seatHoldId);
            if (!seatHold.getCustomerEmail().equals(customerEmail)) {
                return "Cannot find Customer with provided email. Please try again";
            }
            //Checking if seat hold hasn't expired
            for (Seat seat : seatHold.getHeldSeats()) {
                seat.setSeatStatus(SeatStatus.RESERVED);
            }
            //Clean up
            seatsHeld.remove(seatHoldId);
            confirmationMessage = String.format("Seats RESERVED for [ID:%d] by [%s]", seatHoldId, customerEmail);
        } else {
            confirmationMessage = String.format("[ID:%d] doesn't exist, please try again", seatHoldId);
        }
        return confirmationMessage;
    }

}
