package standingOrders;

import java.time.LocalDate;

import bank.storage.Storable;
import users.Customer;

public abstract class StandingOrder implements Storable {
    // public static enum Status {
    //     ACTIVE, EXPIRED, FAILED
    // }

    protected String id;
    protected String title;
    protected String description;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int failedAttempts = 0;
    protected static final int MAX_ATTEMPTS = 3;
    protected double fee;
    protected Customer customer;
    // protected Status status = Status.ACTIVE;

    public StandingOrder(Customer customer, String id, String title, String description, LocalDate startDate, LocalDate endDate,
            double fee) {
        this.customer = customer;
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    public StandingOrder() {

    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public boolean isActive(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public abstract boolean isDue(LocalDate today);

    public abstract void execute(LocalDate today);

    public void registerFailure() {
        failedAttempts++;
    }

    public boolean hasExceededMaxFailures() {
        return failedAttempts >= MAX_ATTEMPTS;
    }

}