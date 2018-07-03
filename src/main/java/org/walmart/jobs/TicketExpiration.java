package org.walmart.jobs;

import org.apache.log4j.Logger;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatStatus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TicketExpiration implements Runnable {
    private final static Logger logger = Logger.getLogger(TicketExpiration.class);
    private final Map<Integer, SeatHold> seatsHeld;
    private final int holdDuration;

    public TicketExpiration(Map<Integer, SeatHold> seatsHeld, int holdDuration) {
        this.seatsHeld = seatsHeld;
        this.holdDuration = holdDuration;
    }

    @Override
    public void run() {
        try {
            this.seatsHeld.values().parallelStream().forEach((seatHold) -> {
                if (isExpired(seatHold.getHeldTime())) {
                    //Clean up
                    logger.info(String.format("%d seconds of inactivity, Tickets will now be RELEASED for [ID:%d]", holdDuration, seatHold.getSeatHoldId()));
                    for (Seat seat : seatsHeld.get(seatHold.getSeatHoldId()).getHeldSeats()) {
                        seat.setSeatStatus(SeatStatus.AVAILABLE);
                    }
                    seatsHeld.remove(seatHold.getSeatHoldId());
                }
            });
        } catch (Exception e) {
            logger.error("Exception in scheduled Ticket Expiration task", e);
        }
    }

    private boolean isExpired(long holdTime) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - holdTime;
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsed, TimeUnit.MILLISECONDS);
        return elapsedSeconds > holdDuration;
    }
}
