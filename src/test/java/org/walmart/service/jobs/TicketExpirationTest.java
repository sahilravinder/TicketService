package org.walmart.service.jobs;

import org.junit.*;
import org.walmart.jobs.TicketExpiration;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TicketExpirationTest {

    private static Map<Integer, SeatHold> seatsHeld = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduledExecutorService;
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    @BeforeClass
    public static void before() throws ParseException {
        SeatHold holdOne = new SeatHold();
        List<Seat> heldSeats = new ArrayList<>();
        for (int seatNum = 1; seatNum <= 5; seatNum++) {
            heldSeats.add(new Seat(1, seatNum));
        }
        holdOne.setCustomerEmail("abc@xyz.com");
        holdOne.setSeatHoldId(1);
        holdOne.setHeldSeats(heldSeats);

        //Back-date order so hold expires
        Date date = df.parse("01/01/2018");
        long millis = date.getTime();
        holdOne.setHeldTime(millis);

        seatsHeld.put(holdOne.getSeatHoldId(), holdOne);

        SeatHold holdTwo = new SeatHold();
        heldSeats = new ArrayList<>();
        for (int seatNum = 1; seatNum <= 5; seatNum++) {
            heldSeats.add(new Seat(2, seatNum));
        }
        holdTwo.setCustomerEmail("def@xyz.com");
        holdTwo.setSeatHoldId(2);
        holdTwo.setHeldSeats(heldSeats);
        holdTwo.setHeldTime(System.nanoTime());
        seatsHeld.put(holdTwo.getSeatHoldId(), holdTwo);

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(new TicketExpiration(seatsHeld, 5), //hold duration set as 5 seconds
                0,
                1,
                TimeUnit.SECONDS);
    }

    @AfterClass
    public static void after() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                   System.out.println("Timed out waiting for Ticket Expiration task to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }

    @Test
    public void getSeatsHeldBeforeExpire() {
        SeatHold firstSeatHold = seatsHeld.get(1);
        SeatHold secondSeatHold = seatsHeld.get(2);
        Assert.assertEquals(5, firstSeatHold.getHeldSeats().size());
        Assert.assertEquals(5, secondSeatHold .getHeldSeats().size());
    }

    @Test
    public void getSeatsHeldOnExpire() throws InterruptedException {
        int totalHeldSeats = seatsHeld.get(1).getHeldSeats().size() + seatsHeld.get(1).getHeldSeats().size();
        Assert.assertEquals(10, totalHeldSeats);

        Thread.sleep(1000); //wait for thread to finish the job.
        Assert.assertFalse(seatsHeld.isEmpty());
        Assert.assertNull(seatsHeld.get(1));
        Assert.assertNotNull(seatsHeld.get(2));
        Assert.assertEquals(seatsHeld.get(2).getHeldSeats().size() , 5);

    }

}
