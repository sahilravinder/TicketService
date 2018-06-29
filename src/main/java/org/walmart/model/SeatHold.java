package org.walmart.model;

import java.util.List;
public class SeatHold {

    private int seatHoldId;
    private List<Seat> heldSeats;
    private String customerEmail;

    public List<Seat> getHeldSeats() {
        return heldSeats;
    }

    public void setHeldSeats(List<Seat> numSeats) {
        this.heldSeats = numSeats;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setSeatHoldId(int seatHoldId) {
        this.seatHoldId = seatHoldId;
    }

    public Integer getSeatHoldId() {
        return this.seatHoldId;
    }

    private boolean isValidHold() {
        return this.customerEmail != null && this.seatHoldId != 0 && this.heldSeats != null;
    }


    @Override
    public String toString() {
        StringBuilder messageBuilder = new StringBuilder();

        if (isValidHold()) {
            messageBuilder.append(String.format("Seat HOLD for [%s]", customerEmail));
            messageBuilder.append(String.format(" [ID:%d]", seatHoldId));
            messageBuilder.append(" Seats[ ");
            for (Seat seat : heldSeats) {
                messageBuilder.append(String.format("{%d, %d}", seat.getRowNum(), seat.getSeatNum()));
                messageBuilder.append(" ");
            }
            messageBuilder.append("]");
        }
        else {
            messageBuilder.append(String.format("Seats currently not available to hold for %s, please try again later", customerEmail));
        }
        return messageBuilder.toString();
    }
}
