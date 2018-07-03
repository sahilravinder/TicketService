package org.walmart.service.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;

import java.util.ArrayList;
import java.util.List;

public class SeatHoldTest {

    private static SeatHold seatHold;

    @BeforeClass
    public static void beforeTest() {

        List<Seat> heldSeats = new ArrayList<>();
        heldSeats.add(new Seat(1,1));
        heldSeats.add(new Seat(1,2));
        heldSeats.add(new Seat(1,3));
        heldSeats.add(new Seat(1,4));
        heldSeats.add(new Seat(1,5));


        seatHold = new SeatHold();
        seatHold.setCustomerEmail("abc@xyz.com");
        seatHold.setSeatHoldId(999);
        seatHold.setHeldSeats(heldSeats);
        seatHold.setHeldTime(System.nanoTime());
    }

    @Test
    public void getHeldSeatsTest() {
        int heldSeatsCount = seatHold.getHeldSeats().size();
        Assert.assertEquals(5, heldSeatsCount);
    }

    @Test
    public void getSeatHoldIdTest() {
        int seatHoldId = seatHold.getSeatHoldId();
        Assert.assertEquals(999, seatHoldId);
    }

    @Test
    public void getCustomerEmailTest() {
        String customerEmail = seatHold.getCustomerEmail();
        Assert.assertEquals("abc@xyz.com", customerEmail);
    }

    @Test
    public void getHeldTimeTest() {
        long seatHeldTime  = seatHold.getHeldTime();
        Assert.assertNotEquals(System.nanoTime(), seatHeldTime);
    }

    @Test
    public void printSeatHoldStatus() {
        String seatHoldMesasge = "Seat HOLD for [abc@xyz.com] [ID:999] Seats[ {1, 1} {1, 2} {1, 3} {1, 4} {1, 5} ]";
        Assert.assertTrue(seatHold.toString().length() > 0);
        Assert.assertEquals(seatHoldMesasge, seatHold.toString());
    }

}


