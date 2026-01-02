package transactions.protocol;

import accounts.BankAccount;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;

public class SepaTransferProtocol implements TransferProtocol {

    private static final String URL = "http://147.27.70.44:3020/transfer/sepa";

    @Override
    public void execute(BankAccount from, BankAccount to, double amount) throws Exception {

        String json = """
        {
          "amount": %f,
          "creditor": {
            "name": "%s",
            "iban": "%s"
          },
          "creditorBank": {
            "bic": "ETHNGRAA",
            "name": "National Bank of Greece"
          },
          "execution": {
            "requestedDate": "%s",
            "charges": "SHA"
          }
        }
        """.formatted(
                amount,
                to.getPrimaryHolder().getLegalName(),
                to.getIban(),
                LocalDate.now()
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
            throw new Exception("SEPA transfer failed");
        }

       
        from.debit(amount);
        to.credit(amount);
    }
}
