package bank.storage;

import java.util.ArrayList;

public class StorableList<T extends Storable> extends ArrayList<T> implements Storable {

    private static final StorableFactory factory = new StorableFactory();

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        for (T item : this) {
            sb.append(item.marshal()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        this.clear();

        String[] lines = data.split("\\R");
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            String type = line.split(",", 2)[0].substring("type:".length());

            Storable inst = factory.createByType(type);
            inst.unmarshal(line);

            this.add((T) inst);
        }
    }
}
