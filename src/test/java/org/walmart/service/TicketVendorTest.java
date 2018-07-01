package org.walmart.service;

import org.junit.*;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketVendorTest {
    private TicketService ticketService = null;

    @Before
    public void beforeTest() {
        //Assign a seat layout
        SeatMap seatMap = new SeatMap(20, 5);
        int holdDuration = 10;
        ticketService = new TicketVendor(seatMap, holdDuration);
    }

    @After
    public void afterTest() {
        try {
            ticketService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAvailableSeatsTest() {
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(100, availableSeats);
    }

    @Test
    public void holdSeatsTest() {
        ticketService.findAndHoldSeats(22, "s@abc.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(78, availableSeats);
    }

    @Test
    public void holdSeatsExpiredTest() throws InterruptedException {
        ticketService.findAndHoldSeats(22, "s@abc.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(78, availableSeats);
        Thread.sleep(12000); //Introduce delay so tickets are released
        availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(100, availableSeats);
    }

    @Test
    public void holdSeatsExpiredMultiTransactionTest() throws InterruptedException {
        ticketService.findAndHoldSeats(10, "s@abc.com");

        SeatHold customer = ticketService.findAndHoldSeats(11, "a@xyz.com");
        ticketService.reserveSeats(customer.getSeatHoldId(), "a@xyz.com");

        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(79, availableSeats);

        Thread.sleep(12000); //Introduce delay so tickets are released

        availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(89, availableSeats);
    }

    @Test
    public void holdMoreThanAvailableTest() {
        SeatHold hold = ticketService.findAndHoldSeats(70, "s@abc.com");
        ticketService.reserveSeats(hold.getSeatHoldId(), "s@abc.com");

        hold = ticketService.findAndHoldSeats(50, "a@xyz.com");
        int availableSeats = ticketService.numSeatsAvailable();
        Assert.assertEquals(30, availableSeats);
        Assert.assertEquals(0, (int) hold.getSeatHoldId());
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

        Object[] heldSeatRows = hold.getHeldSeats().stream().map(Seat::getRowNum).distinct().toArray();
        Object[] heldSeats = hold.getHeldSeats().stream().map(Seat::getSeatNum).distinct().toArray();

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

        heldSeatRows = hold.getHeldSeats().stream().map(Seat::getRowNum).distinct().toArray();
        heldSeats = hold.getHeldSeats().stream().map(Seat::getSeatNum).distinct().toArray();

        seatRows = new ArrayList<Object>() {{
            add(1);
        }};
        seats = new ArrayList<Object>() {{
            add(6);
            add(7);
            add(8);
        }};

        Assert.assertArrayEquals(seatRows.toArray(), heldSeatRows);
        Assert.assertArrayEquals(seats.toArray(), heldSeats);
    }
}

