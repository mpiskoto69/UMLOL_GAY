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

    /**
     * Executes orders due on 'today', prioritizing by "older scheduled attempt first".
     * For our simplified case, we prioritize by startDate then id (stable ordering).
     */
    public void executeDueOrders(LocalDate today) {
        if (today == null) return;

        // Collect due orders
        List<StandingOrder> due = new ArrayList<>();
        for (StandingOrder order : standingOrders) {
            if (order == null) continue;

            if (order.isDue(today) && !order.hasExceededMaxFailuresFor(today)) {
                due.add(order);
            }
        }

        // Priority: older startDate first, then id
        due.sort(Comparator
            .comparing(StandingOrder::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(StandingOrder::getId, Comparator.nullsLast(Comparator.naturalOrder())));

        // Execute
        for (StandingOrder order : due) {
            order.execute(today);
        }
    }
}
