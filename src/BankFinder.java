import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Scanner;

public class BankFinder {
    private static final String NBP_URL = "https://ewib.nbp.pl/plewibnra?dokNazwa=plewibnra.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the first three digits of the bank account: ");
        String userInput = scanner.nextLine().trim();

        if (!userInput.matches("\\d{3}")) {
            System.out.println("Invalid input. Please enter exactly 3 numeric digits.");
            return;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(NBP_URL))
                    .GET()
                    .build();

            HttpResponse<java.io.InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body(), Charset.forName("Windows-1250")))) {

                String line;
                boolean found = false;

                while ((line = reader.readLine()) != null) {
                    if (line.length() < 121) {
                        continue;
                    }

                    String bankName = line.substring(4, 104).trim();
                    String abbreviatedBankNumber = line.substring(118, 121).trim();

                    if (abbreviatedBankNumber.equals(userInput)) {
                        System.out.println("You have an account in: " + bankName);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("No bank found for the given three digits.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading file from NBP: " + e.getMessage());
        }
    }
}
