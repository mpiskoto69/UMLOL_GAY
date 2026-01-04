package standingOrders;
import java.time.LocalDate;
import bank.storage.Storable;
import users.Customer;

public abstract class StandingOrder implements Storable {

    protected String id;
    protected String title;
    protected String description;
    protected LocalDate startDate;
    protected LocalDate endDate;

    // Failure policy
    protected int failedAttempts = 0;
    protected static final int MAX_ATTEMPTS = 3;
    protected LocalDate failureBucketDate = null;
    protected double fee;
    protected Customer customer;

    protected StandingOrder(Customer customer,
                            String id,
                            String title,
                            String description,
                            LocalDate startDate,
                            LocalDate endDate,
                            double fee) {
        this.customer = customer;
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    protected StandingOrder() { }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getFee() { return fee; }
    public Customer getCustomer() { return customer; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public LocalDate getFailureBucketDate() { return failureBucketDate; }
    public void setFailureBucketDate(LocalDate d) { this.failureBucketDate = d; }

    public static int getMaxAttempts() { return MAX_ATTEMPTS; }

    public boolean isActive(LocalDate today) {
        return today != null
            && startDate != null
            && endDate != null
            && !today.isBefore(startDate)
            && !today.isAfter(endDate);
    }

    
    public abstract boolean isDue(LocalDate today);

    
    public abstract void execute(LocalDate today);

    /**
     * Call this when an attempt FAILS.
     * Keeps failures per due-date attempt; resets automatically when due-date changes.
     */
    public void onAttemptFailure(LocalDate today) {
        if (today == null) {
            failedAttempts++;
            return;
        }

        // If failures were tracked for a different attempt date, reset for this new attempt
        if (failureBucketDate == null || !failureBucketDate.equals(today)) {
            failureBucketDate = today;
            failedAttempts = 0;
        }

        failedAttempts++;
    }

    /**
     * Returns true if we should skip the attempt for this specific due-date.
     */
    public boolean hasExceededMaxFailuresFor(LocalDate today) {
        if (today == null) return failedAttempts >= MAX_ATTEMPTS;

        // If the bucket is not today, failures do not apply to today's attempt
        if (failureBucketDate == null || !failureBucketDate.equals(today)) return false;

        return failedAttempts >= MAX_ATTEMPTS;
    }

    public boolean hasExceededMaxFailures() {
        return failedAttempts >= MAX_ATTEMPTS;
    }
}
