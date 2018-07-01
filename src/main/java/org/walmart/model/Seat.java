package org.walmart.model;

public class Seat  {
    private final int rowNum;
    private final int seatNum;
    private SeatStatus seatStatus = SeatStatus.AVAILABLE;

    Seat(int rowNum, int seatNum) {
        this.rowNum = rowNum;
        this.seatNum = seatNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public SeatStatus getSeatStatus() { return seatStatus;}

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

}


