package bank.storage;

import java.util.ArrayList;

import accounts.BusinessAccount;
import accounts.MasterAccount;
import accounts.PersonalAccount;
import standingOrders.PaymentOrder;
import standingOrders.TransferOrder;
import users.Admin;
import users.Company;
import users.Individual;

public class StorableList<T extends Storable> extends ArrayList<T> implements Storable {

	@Override
	public String marshal() {
		StringBuilder sb = new StringBuilder();
		for (T item : this) {
			sb.append(item.marshal()).append("\n");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unmarshal(String data) throws UnMarshalingException {
		String[] lines = data.split("\\R");
		for (String line : lines) {
			if (line.isBlank())
				continue;
			String type = line.split(",", 2)[0].substring("type:".length());
			Storable inst;
			 switch (type) {
                case "Admin":
                    inst = new Admin();
                    break;
                case "Individual":
                    inst = new Individual();
                    break;
                case "Company":
                    inst = new Company();
                    break;
				case "PersonalAccount":
					inst = new PersonalAccount();
					break;
				case "BusinessAccount":
					inst = new BusinessAccount();
					break;
				case "Bill":
					inst = new Bill();
					break;
				case "TransferOrder":
					inst = new TransferOrder();
					break;
				case "PaymentOrder":
					inst = new PaymentOrder();
				case "MasterAccount":
					inst = MasterAccount.getInstance();
					break;
				
				default:
					throw new UnMarshalingException("Unknown type: " + type);
			}
			inst.unmarshal(line);
			this.add((T) inst);
		}
	}
}
