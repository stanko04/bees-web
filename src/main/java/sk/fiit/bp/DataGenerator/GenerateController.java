package sk.fiit.bp.DataGenerator;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.logging.Logger;

@RestController
@Component
public class GenerateController {

    private static final Logger LOGGER = Logger.getLogger(DataGeneratorApplication.class.getName());

    // VONKAJSIA TEPLOTA, VONKAJSIA VLHKOST
    // POSIELANIE KAZDU HODINU
    @Scheduled(fixedRate = 3600000) // 3,600,000 milliseconds = 1 hour
//    @GetMapping("/send-data-weather")
    public void getAndSendWeatherData() {
        final String uri = "http://api.weatherapi.com/v1/current.json?key=3d4c9ec96dd4442cbcf121421230711&q=Trnava&aqi=no";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        // Extract required attributes from the JSON response
        String temp_string = result.substring(result.indexOf("\"temp_c\":") + 9, result.indexOf(",\"temp_f\""));
        String humidity_string = result.substring(result.indexOf("\"humidity\":") + 11, result.indexOf(",\"cloud\""));

        // Convert the extracted strings to integer values
        float temp = Float.parseFloat(temp_string);
        float humidity = Float.parseFloat(humidity_string);

        // Create a JSON string with the extracted data
        String jsonPayload = "{\"teplota_vonkajšia\":" + temp + ", \"vlhkosť_vonkajšia\":" + humidity + "}";

        sendDataToThingsBoard(jsonPayload);

        LOGGER.info("Data was sent: " + jsonPayload);
    }

    // VNUTORNA TEPLOTA, VNUTORNA VLHKOST, FREKVENCIA
    // POSIELANIE KAZDYCH 30 MIN
    @Scheduled(fixedRate = 1800000) // 1,800,000 milliseconds = 30 minutes
//    @GetMapping("/send-data-bee-hive")
    public void generateAndSendBeeHiveTelemetry() {
        Random random = new Random();

        // Generate random float values for temperature and humidity within the specified ranges
        float temperature = 30.0f + random.nextFloat() * (35.0f - 30.0f);
        float humidity = 50.0f + random.nextFloat() * (70.0f - 50.0f);
        float frequency = 230.0f + random.nextFloat() * (270.0f - 230.0f);

        float temperature_rounded = (float) (Math.round(temperature * 10.0) / 10.0);
        float humidity_rounded = (float) (Math.round(humidity * 10.0) / 10.0);
        float frequency_rounded = (float) (Math.round(frequency * 10.0) / 10.0);


        // Create a JSON string with the generated data
        String jsonPayload = "{ \"teplota_vnútorná\":" + temperature_rounded + ", " +
                "\"vlhkosť_vnútorná\":" + humidity_rounded + ", " + "\"frekvencia_Hz\":" + frequency_rounded + "}";

        sendDataToThingsBoard(jsonPayload);

        LOGGER.info("Data was sent: " + jsonPayload);
    }


    // HMOTNOST A NAKLONENIE
    // POSIELANIE RAZ ZA DEN
    @Scheduled(fixedRate = 86400000) // 86,400,000 milliseconds = 24 hours
//    @GetMapping("/send-data-weight-rollover")
    public void generateAndSendWeightRollover() {
        Random random = new Random();

        float weight = 30.0f + random.nextFloat() * (40.0f - 30.0f);
        float weight_rounded = (float) (Math.round(weight * 10.0) / 10.0);

        boolean rollover = random.nextDouble() < 0.9;

        // Create a JSON string with the extracted data
        String jsonPayload = "{\"hmotnosť\":" + weight_rounded + ", \"prevrátenie\":" + rollover + "}";

        sendDataToThingsBoard(jsonPayload);

        LOGGER.info("Data was sent: " + jsonPayload);
    }

    public void sendDataToThingsBoard(String jsonPayload) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare the headers for the POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with headers and payload
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange("http://165.22.17.201:8080/api/v1/Axq7ebShnlQGXKW69PbD/telemetry", HttpMethod.POST, request, String.class);
    }




}