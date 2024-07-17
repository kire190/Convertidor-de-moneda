import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CurrencyConverter {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/3ea338fc0d4c66f53a8d52e7/latest/USD";

    private static Map<String, BigDecimal> exchangeRates;

    public static void main(String[] args) {
        initializeRates();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Conversor de Monedas ===");
            System.out.println("1. USD a ARS");
            System.out.println("2. USD a BRL");
            System.out.println("3. USD a COP");
            System.out.println("4. Salir");
            System.out.print("Elija una opción: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    convert("USD", "ARS");
                    break;
                case 2:
                    convert("USD", "BRL");
                    break;
                case 3:
                    convert("USD", "COP");
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void initializeRates() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject ratesObject = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonObject("rates");
                exchangeRates = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : ratesObject.entrySet()) {
                    exchangeRates.put(entry.getKey(), entry.getValue().getAsBigDecimal());
                }
                System.out.println("Tasas de cambio actualizadas correctamente.");
            } else {
                System.out.println("No se pudo obtener las tasas de cambio. Código de estado: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void convert(String fromCurrency, String toCurrency) {
        BigDecimal fromRate = exchangeRates.get(fromCurrency);
        BigDecimal toRate = exchangeRates.get(toCurrency);

        if (fromRate != null && toRate != null) {
            BigDecimal amountUSD = BigDecimal.valueOf(100); // Ejemplo: convertir $100 USD
            BigDecimal convertedAmount = amountUSD.multiply(toRate).divide(fromRate, 2, BigDecimal.ROUND_HALF_UP);
            System.out.printf("$%s USD equivale a %s %s\n", amountUSD.toString(), convertedAmount.toString(), toCurrency);
        } else {
            System.out.println("No se encontraron tasas de cambio para las monedas especificadas.");
        }
    }
}
