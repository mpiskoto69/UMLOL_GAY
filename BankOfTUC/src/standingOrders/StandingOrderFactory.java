package standingOrders;

import bank.storage.UnMarshalingException;

public class StandingOrderFactory {

    public StandingOrder createEmptyByType(String type) throws UnMarshalingException {
        return switch (type) {
            case "PaymentOrder" -> new PaymentOrder();
            case "TransferOrder" -> new TransferOrder();
            default -> throw new UnMarshalingException("Unknown order type: " + type);
        };
    }

    public StandingOrder fromLine(String line) throws UnMarshalingException {
        // line starts like: type:PaymentOrder,...
        String type = line.split(",", 2)[0].substring("type:".length());
        StandingOrder so = createEmptyByType(type);
        so.unmarshal(line);
        return so;
    }
}
