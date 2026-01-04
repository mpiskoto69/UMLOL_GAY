package bank.storage.dao;

import bank.storage.StorableList;
import bank.storage.UnMarshalingException;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import standingOrders.StandingOrder;
import standingOrders.StandingOrderFactory;

public class StandingOrderDao {

    private final Path folder;
    private final StandingOrderFactory factory = new StandingOrderFactory();

    public StandingOrderDao(Path folder) {
        this.folder = folder;
    }

    public StorableList<StandingOrder> loadFile(String fileName) throws IOException, UnMarshalingException {
        Path file = folder.resolve(fileName);
        StorableList<StandingOrder> list = new StorableList<>();
        if (!Files.exists(file))
            return list;

        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank())
                    continue;
                list.add(factory.fromLine(line));
            }
        }
        return list;
    }

    public void saveByStatus(List<StandingOrder> all, LocalDate simulatedToday) throws IOException {
        Files.createDirectories(folder);

        Path active = folder.resolve("active.csv");
        Path expired = folder.resolve("expired.csv");
        Path failed = folder.resolve("failed.csv");

        // overwrite each time
        Files.writeString(active, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(expired, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(failed, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for (StandingOrder so : all) {
            Path out;
            if (so.hasExceededMaxFailures())
                out = failed;
            else if (!so.isActive(simulatedToday))
                out = expired;
            else
                out = active;

            Files.writeString(out, so.marshal() + System.lineSeparator(), StandardOpenOption.APPEND);
        }
    }
}
