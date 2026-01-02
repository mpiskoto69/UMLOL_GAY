package transactions.protocol;

import accounts.BankAccount;
import java.net.URI;
import java.net.http.*;

public class SwiftTransferProtocol implements TransferProtocol {

    private static final String URL = "http://147.27.70.44:3020/transfer/swift";

    @Override
    public void execute(BankAccount from, BankAccount to, double amount) throws Exception {

        String json = """
        {
          "currency": "EUR",
          "amount": %f,
          "beneficiary": {
            "name": "%s",
            "address": "Unknown",
            "account": "%s"
          },
          "beneficiaryBank": {
            "name": "Foreign Bank",
            "swiftCode": "BARCGB22",
            "country": "UK"
          },
          "fees": {
            "chargingModel": "SHA"
          }
        }
        """.formatted(
                amount,
                to.getPrimaryHolder().getLegalName(),
                to.getIban()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 || !response.body().contains("\"status\":\"success\"")) {
            throw new Exception("SWIFT transfer failed");
        }

        from.debit(amount);
        to.credit(amount);
    }
}
