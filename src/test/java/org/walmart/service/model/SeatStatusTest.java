package org.walmart.service.model;

import org.junit.Assert;
import org.junit.Test;
import org.walmart.model.SeatStatus;

public class SeatStatusTest {

    private SeatStatus seatStatus;

    @Test
    public void getSeatStatusAvailable() {
        this.seatStatus = SeatStatus.AVAILABLE;
        Assert.assertEquals(SeatStatus.AVAILABLE, this.seatStatus);
    }

    @Test
    public void getSeatStatusReserved() {
        this.seatStatus = SeatStatus.RESERVED;
        Assert.assertEquals(SeatStatus.RESERVED, this.seatStatus);
    }

    @Test
    public void getSeatStatusHold() {
        this.seatStatus = SeatStatus.HOLD;
        Assert.assertEquals(SeatStatus.HOLD, this.seatStatus);
    }

}
