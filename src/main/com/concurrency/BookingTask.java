
package main.com.concurrency;

public class BookingTask implements Runnable {

    private int propertyId;
    private String buyerName;

    public BookingTask(int propertyId, String buyerName) {
        this.propertyId = propertyId;
        this.buyerName = buyerName;
    }

    @Override
    public void run() {

        if (PropertyLockManager.lockProperty(propertyId)) {

            try {
                System.out.println(buyerName + " is booking property " + propertyId);

                Thread.sleep(2000);

                System.out.println("Property " + propertyId + " booked successfully by " + buyerName);

            } catch (InterruptedException e) {
                System.out.println("Booking interrupted");
                Thread.currentThread().interrupt();

            } finally {
                PropertyLockManager.unlockProperty(propertyId);
            }

        } else {
            System.out.println("Property " + propertyId + " is currently being booked. Try again later.");
        }
    }
}
