package bank.storage.dao;

import standingOrders.Bill;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class BillDao {

    private final Path folder;

    public BillDao(Path folder) {
        this.folder = folder;
    }

    public List<Bill> loadAll() throws IOException {
        List<Bill> result = new ArrayList<>();
        loadInto(result, folder.resolve("issued.csv"), false);
        loadInto(result, folder.resolve("paid.csv"), true);
        return result;
    }

    private void loadInto(List<Bill> out, Path file, boolean markPaid) throws IOException {
        if (!Files.exists(file)) return;

        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                Bill b = new Bill();
                try {
                    b.unmarshal(line);
                    if (markPaid) b.markAsPaid();
                    out.add(b);
                } catch (Exception ex) {
                    // skip or log malformed
                }
            }
        }
    }

    public void saveAll(List<Bill> bills) throws IOException {
        Files.createDirectories(folder);

        Path issued = folder.resolve("issued.csv");
        Path paid   = folder.resolve("paid.csv");

        try (
    BufferedWriter iw = Files.newBufferedWriter(
        issued,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    );
    BufferedWriter pw = Files.newBufferedWriter(
        paid,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    )
){
            for (Bill b : bills) {
                if (b.isPaid()) {
                    pw.write(b.marshal());
                    pw.newLine();
                } else {
                    iw.write(b.marshal());
                    iw.newLine();
                }
            }
        }
    }
}
