package bank.storage;

import accounts.BusinessAccount;
import accounts.MasterAccount;
import accounts.PersonalAccount;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import users.Admin;
import users.Company;
import users.Individual;

public class StorableFactory {

    private final Map<String, Supplier<Storable>> registry = new HashMap<>();

    public StorableFactory() {
        registry.put("Admin", Admin::new);
        registry.put("Individual", Individual::new);
        registry.put("Company", Company::new);

        registry.put("PersonalAccount", PersonalAccount::new);
        registry.put("BusinessAccount", BusinessAccount::new);
        registry.put("MasterAccount", MasterAccount::getInstance);

        registry.put("Bill", Bill::new);

       
    }

    public Storable createByType(String type) throws UnMarshalingException {
        Supplier<Storable> s = registry.get(type);
        if (s == null) throw new UnMarshalingException("Unknown type: " + type);
        return s.get();
    }
}
