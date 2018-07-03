package org.walmart.service.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.walmart.model.Seat;
import org.walmart.model.SeatStatus;

public class SeatTest {

    private Seat seat;

    @Before
    public void beforeTest() {
        this.seat = new Seat(5, 20);
    }

    @Test
    public void getRowNumTest() {
        int rowNum = this.seat.getRowNum();
        Assert.assertEquals(5, rowNum);
    }

    @Test
    public void getSeatNumTest() {
        int seatNum = this.seat.getSeatNum();
        Assert.assertEquals(20, seatNum);
    }

    @Test
    public void getSeatStatusTest() {
        SeatStatus seatStatus = this.seat.getSeatStatus();
        Assert.assertEquals(SeatStatus.AVAILABLE, seatStatus);
    }


}
