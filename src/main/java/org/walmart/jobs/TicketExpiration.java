package org.walmart.jobs;

import org.apache.log4j.Logger;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatStatus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TicketExpiration implements Runnable {
    private final static Logger logger = Logger.getLogger(TicketExpiration.class);
    private final Map<Integer, SeatHold>seatsHeld;
    private final int holdDuration;

    public TicketExpiration(Map<Integer, SeatHold> seatsHeld, int holdDuration) {
        this.seatsHeld = seatsHeld;
        this.holdDuration = holdDuration;
    }

    @Override
    public void run() {
        for (SeatHold seatHold : this.seatsHeld.values()) {
            if (isExpired(seatHold.getHeldTime())) {
                //Clean up
                logger.info(String.format("%d seconds of inactivity, Tickets will now be RELEASED for [ID:%d]", holdDuration, seatHold.getSeatHoldId()));
                for (Seat seat : seatsHeld.get(seatHold.getSeatHoldId()).getHeldSeats()) {
                    seat.setSeatStatus(SeatStatus.AVAILABLE);
                }
                seatsHeld.remove(seatHold.getSeatHoldId());
            }
        }
    }

    private boolean isExpired(long holdTime) {
        long currentTime = System.nanoTime();
        long elapsed = currentTime - holdTime;
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
        return elapsedSeconds > holdDuration;
    }
}
