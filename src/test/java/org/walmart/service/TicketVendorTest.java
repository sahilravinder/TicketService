package org.walmart.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.walmart.model.Seat;
import org.walmart.model.SeatHold;
import org.walmart.model.SeatMap;

import java.util.ArrayList;
import java.util.List;


public class TicketVendorTest {
    private TicketService ticketService = null;
    private static final int holdDuration = 5;

    @Before
    public void beforeTest() {
        //Assign a seat layout for testing
        SeatMap seatMap = new SeatMap(5, 5);
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

//    @Test
//    public void reserveSeatsMultiThreadedTest() throws InterruptedException {
//
//        ExecutorService executor = Executors.newCachedThreadPool();
//
////        CustomerHoldSeats firstCustomer = new CustomerHoldSeats(this.ticketService, 5, "a@xyz.com");
////        CustomerHoldSeatsDelay secondCustomer = new CustomerHoldSeatsDelay(this.ticketService, 10, "b@xyz.com");
////        CustomerHoldSeats thirdCustomer = new CustomerHoldSeats(this.ticketService, 10, "b@xyz.com");
////
////        List<Runnable> tasks = new ArrayList<>();
////        tasks.add(firstCustomer);
////        tasks.add(secondCustomer);
////        tasks.add(thirdCustomer);
//
//        List<Future> futures = new ArrayList<Future>();
//
//        futures.add(executor.submit(new CustomerHoldSeats(this.ticketService, 5, "a@xyz.com") {
//            public Void call() throws IOException {
//                int availableSeats = ticketService.numSeatsAvailable();
//                Assert.assertEquals(20, availableSeats);
//                return null;
//            }
//        }));
//
//        futures.add(executor.submit(new CustomerHoldSeatsDelay(this.ticketService, 10, "b@xyz.com") {
//            public Void call() throws IOException {
//                int availableSeats = ticketService.numSeatsAvailable();
//                Assert.assertEquals(20, availableSeats);
//                return null;
//            }
//        }));
//
//        futures.add(executor.submit(new CustomerHoldSeats(this.ticketService, 10, "b@xyz.com") {
//            public Void call() throws IOException {
//                int availableSeats = ticketService.numSeatsAvailable();
//                Assert.assertEquals(10, availableSeats);
//                return null;
//            }
//        }));
//
////        executor.execute(firstCustomer);
////        executor.execute(secondCustomer);
////        executor.execute(thirdCustomer);
//
//        //Thread.sleep(10000);
//
//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//
////        new Thread(firstCustomer).start();
////        new Thread(secondCustomer).start();
////        new Thread(thirdCustomer).start();
//
////        int availableSeats = ticketService.numSeatsAvailable();
////        Assert.assertEquals(10, availableSeats);
//    }

//    class CustomerHoldSeats implements Runnable {
//
//
//        private TicketService ticketService;
//        private int numSeats;
//        private String customerEmail;
//
//        public CustomerHoldSeats(TicketService ticketService, int numSeats, String customerEmail) {
//            this.ticketService = ticketService;
//            this.numSeats = numSeats;
//            this.customerEmail = customerEmail;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("CustomerHoldSeats called from thread");
//            SeatHold hold = this.ticketService.findAndHoldSeats(this.numSeats, this.customerEmail);
//            //this.ticketService.reserveSeats(hold.getSeatHoldId(), this.customerEmail);
//        }
//    }
//
//    class CustomerHoldSeatsDelay implements Runnable {
//        private TicketService ticketService;
//        private int numSeats;
//        private String customerEmail;
//
//        public CustomerHoldSeatsDelay(TicketService ticketService, int numSeats, String customerEmail) {
//            this.ticketService = ticketService;
//            this.numSeats = numSeats;
//            this.customerEmail = customerEmail;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("CustomerHoldSeatsDelay called from thread");
//            this.ticketService.findAndHoldSeats(this.numSeats, this.customerEmail);
//            try {
//                TimeUnit.SECONDS.sleep(6000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
}

