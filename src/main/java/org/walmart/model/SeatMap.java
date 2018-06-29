package org.walmart.model;

import java.util.*;
import java.util.stream.Collectors;

public class SeatMap {
    private LinkedHashSet<Seat> seatMap;

    public SeatMap(int numSeats, int totalRows) {
        initializeSeatMap(numSeats, totalRows);
    }

    public Set<Seat> getSeatMap() {
        return seatMap;
    }

    public List<Integer> getRows() {
        return this.seatMap.stream().map(x -> x.getRowNum()).distinct().collect(Collectors.toList());
    }

    public List<Seat> getSeatsByStatus(SeatStatus seatSeatStatus) {
        return this.seatMap.stream().filter(x -> x.getSeatStatus() == seatSeatStatus).collect(Collectors.toList());
    }

    public List<Seat> getSeatInfoForRow(int row) {
        return this.seatMap.stream().filter(x -> x.getRowNum() == row).collect(Collectors.toList());
    }


    public Integer getAvailableSeats() {
        return (int)this.getSeatMap().stream().filter(x -> x.getSeatStatus() == SeatStatus.AVAILABLE).count();
    }

    private void initializeSeatMap(int numSeats, int totalRows) {
        if (this.seatMap == null) {
            this.seatMap = new LinkedHashSet<>();

            for (int i = 1; i <= totalRows; i++) {
                for(int j = 1; j <= numSeats; j ++ ) {
                    this.seatMap.add(new Seat(i, j, SeatStatus.AVAILABLE));
                }
            }
        }
    }

    public String currentSeatAvailability() {
        StringBuilder seatMapBuilder = new StringBuilder();
        seatMapBuilder.append(String.format("Seats Available: %d \n", getAvailableSeats()));
        List<Integer> rows = getRows();
        for (Integer row: rows) {
            List<Seat> seats = getSeatInfoForRow(row);
            for(Seat seat: seats) {
                if(seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                    seatMapBuilder.append("A");
                }
                else if(seat.getSeatStatus() == SeatStatus.RESERVED) {
                    seatMapBuilder.append("R");
                }
                else if(seat.getSeatStatus() == SeatStatus.HOLD) {
                    seatMapBuilder.append("H");
                }
            }
            seatMapBuilder.append("\n");
        }
        return seatMapBuilder.toString();
    }
}
