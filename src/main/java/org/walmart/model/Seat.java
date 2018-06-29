package org.walmart.model;

public class Seat  {
    private int rowNum;
    private int seatNum;
    private SeatStatus seatStatus;

    public Seat(int rowNum, int seatNum, SeatStatus seatStatus) {
        this.rowNum = rowNum;
        this.seatNum = seatNum;
        this.seatStatus = seatStatus;
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


