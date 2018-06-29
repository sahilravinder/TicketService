package org.walmart.org.walmart.pos;

import org.apache.log4j.Logger;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;
import org.walmart.service.TicketVendor;
import org.walmart.service.TicketService;
import java.util.Scanner;


public class POS {
    final static Logger logger = Logger.getLogger(POS.class);

    public static void main(String[] args) {
        //Configure a Seating layout for the venue
        SeatMap seatLayout = new SeatMap(100, 5);
        //Set hold duration(seconds) for tickets
        int holdDuration = 50;

        TicketService transaction = new TicketVendor(seatLayout, holdDuration);

        Scanner scan = new Scanner(System.in);
        logger.info("*** Ticketing System ***");
        boolean accept = true;
        String options = "Choose 1. Available Seats 2. Hold Seats 3. Reserve Seats 4. Exit";
        while (accept) {
            logger.info(options);
            String input = scan.next();
            if (!tryParseInt(input)) {
                logger.error("Enter a valid option");
                continue;
            }
            int param = Integer.parseInt(input);
            switch (param) {
                case 1:
                    logger.info(seatLayout.currentSeatAvailability());
                    break;
                case 2:
                    logger.info("HOLD SEATS");
                    logger.info("Number of seats to hold ?");
                    String numSeats = scan.next();
                    logger.info("Enter Customer Email ");
                    String customerHold = scan.next();
                    SeatHold hold = transaction.findAndHoldSeats(Integer.parseInt(numSeats), customerHold);
                    logger.info(hold);
                    break;
                case 3:
                    logger.info("RESERVE SEATS");
                    logger.info("Enter HOLD ID");
                    String holdId = scan.next();
                    logger.info("Enter Associated Customer Email");
                    String customerReserve = scan.next();
                    String reserved = transaction.reserveSeats(Integer.parseInt(holdId), customerReserve);
                    logger.info(reserved);
                    break;
                case 4:
                    accept = false;
                    break;
                default:
                    logger.info("Enter a valid option");
            }
        }
        scan.close();
    }

    private static boolean tryParseInt(String value) {
        if (value == null || value.isEmpty())
            return false;
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
