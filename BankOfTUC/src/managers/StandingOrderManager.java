package managers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bank.storage.StorableList;
import standingOrders.StandingOrder;

public class StandingOrderManager {
    private static final StandingOrderManager instance = new StandingOrderManager();
    private final StorableList<StandingOrder> standingOrders = new StorableList<>();

    private StandingOrderManager() { }

    public static StandingOrderManager getInstance() {
        return instance;
    }

    public void addOrder(StandingOrder order) {
        if (order != null) standingOrders.add(order);
    }

    public StorableList<StandingOrder> getAllOrders() {
        return standingOrders;
    }

    public void addOrders(StorableList<StandingOrder> orders) {
        if (orders != null) this.standingOrders.addAll(orders);
    }

    
    public void executeDueOrders(LocalDate today) {
        if (today == null) return;

        // Collect due orders
        List<StandingOrder> due = new ArrayList<>();
        for (StandingOrder order : standingOrders) {
            if (order == null) continue;

            if (order.isDue(today) && !order.hasExceededMaxFailures()) {
                due.add(order);
            }
        }

        due.sort(Comparator
            .comparing(StandingOrder::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(StandingOrder::getId, Comparator.nullsLast(Comparator.naturalOrder())));

        // Execute
        for (StandingOrder order : due) {
            order.execute(today);
        }
    }
public static class ExecutionReport {
    public final LocalDate date;
    public final List<String> success = new ArrayList<>();
    public final List<String> failed = new ArrayList<>();
    public ExecutionReport(LocalDate date) { this.date = date; }
}
public ExecutionReport executeDueOrdersWithReport(LocalDate today) {
    ExecutionReport rep = new ExecutionReport(today);
    if (today == null) return rep;

    List<StandingOrder> due = new ArrayList<>();
    for (StandingOrder order : standingOrders) {
        if (order == null) continue;

        if (order.isDue(today) && !order.hasExceededMaxFailuresFor(today)) {
            due.add(order);
        }
    }

    due.sort(Comparator
        .comparing(StandingOrder::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(StandingOrder::getId, Comparator.nullsLast(Comparator.naturalOrder())));

    for (StandingOrder order : due) {
        int beforeFails = order.getFailedAttempts();

        order.execute(today);

        boolean failedNow =
            order.getFailureBucketDate() != null
            && order.getFailureBucketDate().equals(today)
            && order.getFailedAttempts() > beforeFails;

        String label = order.getClass().getSimpleName()
                + " | id=" + order.getId()
                + " | " + order.getTitle();

        if (failedNow) rep.failed.add(label);
        else rep.success.add(label);
    }

    return rep;
}

public void clearAll() {
    standingOrders.clear();
}

}
