package managers;

import java.time.LocalDate;

import bank.storage.StorableList;
import standingOrders.StandingOrder;

public class StandingOrderManager {
    private static final StandingOrderManager instance = new StandingOrderManager();
    private StorableList<StandingOrder> standingOrders = new StorableList<>();

    private StandingOrderManager() {}

    public static StandingOrderManager getInstance() {
        return instance;
    }

    public void addOrder(StandingOrder order) {
        standingOrders.add(order);
    }

    public StorableList<StandingOrder> getAllOrders() {
        return standingOrders;
    }

    public void executeDueOrders(LocalDate today) {
        for (StandingOrder order : standingOrders) {
            if (order.isDue(today) && !order.hasExceededMaxFailures()) {
                order.execute(today);
            }
        }
    }

    public void addOrders(StorableList<StandingOrder> orders)
    {
        this.standingOrders.addAll(orders);
    }
}
