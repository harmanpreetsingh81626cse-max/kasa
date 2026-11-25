package main.com.concurrency;

public class NotificationTask implements Runnable {

    private String message;

    public NotificationTask(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println("\n[NOTIFICATION] " + message);
    }

    public void sendNotification() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
