package org.walmart.service;

import org.apache.log4j.Logger;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;
import org.walmart.model.SeatStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketVendor implements TicketService {
    final static Logger logger = Logger.getLogger(TicketVendor.class);

    private SeatMap seatMap;
    private int holdDuration = 5;
    private Map<Integer, SeatHold> seatsHeld = new HashMap<>();
    private Map<Integer, Timer> customerHoldTimer = new HashMap<>();
    private static AtomicInteger seatHoldId = new AtomicInteger(0);

    public TicketVendor(SeatMap seatMap, int holdDuration) {
        this.seatMap = seatMap;
        this.holdDuration = holdDuration;
    }

    public int numSeatsAvailable() {
        return this.seatMap.getAvailableSeats();
    }

    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        //Check if hold is already placed by customer
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

            seatHold.setHeldSeats(seatsBooked);
            seatHold.setSeatHoldId(seatHoldId.incrementAndGet());
            seatsHeld.put(seatHold.getSeatHoldId(), seatHold);

            if (!customerHoldTimer.keySet().contains(seatHold.getSeatHoldId())) {
                Timer holdTimer = new Timer();
                holdTimer.schedule(new ExpireTask(seatHold.getSeatHoldId()), holdDuration * 1000);
                customerHoldTimer.put(seatHold.getSeatHoldId(), holdTimer);
            }
        }
        return seatHold;
    }

    public String reserveSeats(int seatHoldId, String customerEmail) {
        logger.info(String.format("Requested to RESERVE Seats with [ID:%d] by [%s]", seatHoldId, customerEmail));
        String confirmationMessage;
        if (this.seatsHeld.containsKey(seatHoldId)) {
            SeatHold seatHold = this.seatsHeld.get(seatHoldId);
            if (!seatHold.getCustomerEmail().equals(customerEmail)) {
                return "Cannot find Customer with provided email. Please try again";
            }
            if (seatHold != null) { //Checking if seat hold hasn't expired
                for (Seat seat : seatHold.getHeldSeats()) {
                    seat.setSeatStatus(SeatStatus.RESERVED);
                }
                //Clean up
                cleanup(seatHoldId);
                confirmationMessage = String.format("Seats RESERVED for [ID:%d] by [%s]", seatHoldId, customerEmail);
            } else {
                confirmationMessage = String.format("[ID:%d] HOLD expired, please try again", seatHoldId);
            }
        } else {
            confirmationMessage = String.format("[ID:%d] doesn't exist, please try again", seatHoldId);
        }
        return confirmationMessage;
    }

    private void cleanup(int seatHoldId) {
        this.customerHoldTimer.get(seatHoldId).cancel();  //Terminate the timer thread
        this.seatsHeld.remove(seatHoldId); //Un-hold seats
        this.customerHoldTimer.remove(seatHoldId);  //Remove timer
    }

    class ExpireTask extends TimerTask {

        private int seatHoldId;

        public ExpireTask(int seatHoldId) {
            this.seatHoldId = seatHoldId;
        }

        public void run() {
            logger.info(String.format("%d seconds of inactivity, Tickets will now be RELEASED for [ID: %d]", holdDuration, seatHoldId));
            for (Seat seat : seatsHeld.get(seatHoldId).getHeldSeats()) {
                seat.setSeatStatus(SeatStatus.AVAILABLE);
            }
            //Clean up
            cleanup(seatHoldId);
        }
    }

}
