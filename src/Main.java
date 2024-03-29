import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import controllers.OperationController;
import java.util.Map;
import utils.ConfigLoader;

public class Main {
    public static void main(String[] args) {
        // Load API key and API secret from config.env
        Map<String, String> config = ConfigLoader.loadConfig("config.env");
        String apiKey = config.get("API_KEY");
        String apiSecret = config.get("API_SECRET");

        OperationController operationController = new OperationController(apiKey, apiSecret);
        try {
            while (true) {
                System.out.println("_____________________________________________________");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println("Executing the routine at: " + dtf.format(now));
                operationController.execute();
                Thread.sleep(10000); // 10 seconds
            }
        } catch (InterruptedException ex) {
            // Handle interruption
        }
    }
}
