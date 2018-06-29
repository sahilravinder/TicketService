package org.walmart.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;

import java.util.ArrayList;
import java.util.List;


public class TicketVendorTest {
    private TicketService ticketService = null;
    private SeatMap seatMap = null;
    private static final int holdDuration = 5;

    @Before
    public void beforeTest() {
        //Assign a seat layout for testing
        seatMap = new SeatMap(5, 5);
        ticketService = new TicketVendor(seatMap, holdDuration);
    }

    @Test
    public void getAvailableSeatsTest() {
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(25, availableSeats);
    }

    @Test
    public void holdSeatsTest() {
        ticketService.findAndHoldSeats(22, "s@abc.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(3, availableSeats);
    }

    @Test
    public void holdSeatsExpiredTest() throws InterruptedException {
        ticketService.findAndHoldSeats(22, "s@abc.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(3, availableSeats);
        Thread.sleep(7000);
        availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(25, availableSeats);
    }

    @Test
    public void holdSeatsExpiredMultiTransactionTest() throws InterruptedException {
        ticketService.findAndHoldSeats(10, "s@abc.com");
        ticketService.findAndHoldSeats(11, "a@xyz.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(4, availableSeats);
        Thread.sleep(7000);
        availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(25, availableSeats);
    }

    @Test
    public void holdMoreThanAvailableTest() {
        SeatHold hold = ticketService.findAndHoldSeats(22, "s@abc.com");
        ticketService.reserveSeats(hold.getSeatHoldId(), "s@abc.com");

        hold = ticketService.findAndHoldSeats(10, "a@xyz.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(3, availableSeats);
        Assert.assertTrue(hold.getSeatHoldId() == 0);
        Assert.assertNull(hold.getHeldSeats());
    }

    @Test
    public void reserveWithInvalidIdTest() {
        String message = ticketService.reserveSeats(10021319, "s@abc.com");
        Assert.assertEquals("[ID:10021319] doesn't exist, please try again", message);
    }

    @Test
    public void reserveWithInvalidEmail() {
        SeatHold hold = ticketService.findAndHoldSeats(22, "s@abc.com");
        String message = ticketService.reserveSeats(hold.getSeatHoldId(), "a@xyz.com");
        Assert.assertEquals("Cannot find Customer with provided email. Please try again", message);
    }

    @Test
    public void getBestSeatsTest() {
        SeatHold hold = ticketService.findAndHoldSeats(5, "s@abc.com");

        Object[] heldSeatRows = hold.getHeldSeats().stream().map(x -> x.getRowNum()).distinct().toArray();
        Object[] heldSeats = hold.getHeldSeats().stream().map(x -> x.getSeatNum()).distinct().toArray();

        List<Object> seatRows = new ArrayList<Object>() {{
            add(1);
        }};
        List<Object> seats = new ArrayList<Object>() {{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
        }};

        Assert.assertArrayEquals(seatRows.toArray(), heldSeatRows);
        Assert.assertArrayEquals(seats.toArray(), heldSeats);

        hold = ticketService.findAndHoldSeats(3, "a@xyz.com");

        heldSeatRows = hold.getHeldSeats().stream().map(x -> x.getRowNum()).distinct().toArray();
        heldSeats = hold.getHeldSeats().stream().map(x -> x.getSeatNum()).distinct().toArray();

        seatRows = new ArrayList<Object>() {{
            add(2);
        }};
        seats = new ArrayList<Object>() {{
            add(1);
            add(2);
            add(3);
        }};

        Assert.assertArrayEquals(seatRows.toArray(), heldSeatRows);
        Assert.assertArrayEquals(seats.toArray(), heldSeats);
    }
}