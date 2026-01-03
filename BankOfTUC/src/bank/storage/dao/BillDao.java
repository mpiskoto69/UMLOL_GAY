package bank.storage.dao;

import bank.storage.Bill;

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
        if (!Files.exists(folder)) return result;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {
            for (Path file : stream) {
                boolean markPaidFile = file.getFileName().toString().equalsIgnoreCase("paid.csv");

                try (BufferedReader r = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        if (line.isBlank()) continue;

                        Bill b = new Bill();
                        try {
                            b.unmarshal(line);          // parse
                            if (markPaidFile) b.markAsPaid();
                            result.add(b);
                        } catch (Exception ex) {
                            // ΚΑΝΕ LOG για να βλέπεις τι πετάει έξω
                            System.err.println("Bad bill line in " + file.getFileName() + ": " + line);
                        }
                    }
                }
            }
        }
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
                    System.err.println("Malformed bill line: " + line);
                    System.err.println("Reason: " + ex.getMessage());
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
        StandardOpenOption.TRUNCATE_EXISTING);

        BufferedWriter pw = Files.newBufferedWriter(
        paid,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)){
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
