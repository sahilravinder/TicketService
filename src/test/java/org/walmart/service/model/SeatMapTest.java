package org.walmart.service.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.walmart.model.Seat;
import org.walmart.model.SeatMap;
import org.walmart.model.SeatStatus;

import java.util.List;

public class SeatMapTest {

    private SeatMap seatMap;

    @Before
    public void beforeTest() {
        this.seatMap = new SeatMap(20, 5);
    }

    @Test
    public void getTotalAvailableSeats() {
        int availableSeats = this.seatMap.getAvailableSeats();
        Assert.assertEquals(availableSeats, 100);
    }

    @Test
    public void getSeatsByAvailableTest() {
        List<Seat> availableSeats = this.seatMap.getSeatsByStatus(SeatStatus.AVAILABLE);
        Assert.assertEquals(availableSeats.size(), 100);
    }

    @Test
    public void getSeatsByReservedTest() {
        List<Seat> availableSeats = this.seatMap.getSeatsByStatus(SeatStatus.RESERVED);
        Assert.assertNotEquals(availableSeats.size(), 100);
    }

    @Test
    public void getSeatsByHoldTest() {
        List<Seat> availableSeats = this.seatMap.getSeatsByStatus(SeatStatus.HOLD);
        Assert.assertNotEquals(availableSeats.size(), 100);
    }

    @Test
    public void getSeatAvailabilityString() {
        String seatMapString = this.seatMap.currentSeatAvailability();
        Assert.assertNotNull(seatMapString);
        Assert.assertTrue(seatMapString.length() > 0);
    }
}
