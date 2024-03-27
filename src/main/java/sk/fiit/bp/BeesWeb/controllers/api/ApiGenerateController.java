package sk.fiit.bp.BeesWeb.controllers.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sk.fiit.bp.BeesWeb.BeesWebApplication;
import sk.fiit.bp.BeesWeb.service.DeviceService;
import sk.fiit.bp.BeesWeb.model.database.Device;
import sk.fiit.bp.BeesWeb.repository.DeviceRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@Component
@Slf4j
public class ApiGenerateController {

    @Autowired
    private final DeviceRepository deviceRepository;

    @Autowired
    private DeviceService deviceService;

    float WEIGHT = 20.0f;
    int BATTERY = 80;

    private static final Logger LOGGER = Logger.getLogger(BeesWebApplication.class.getName());

    public ApiGenerateController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // VONKAJSIA TEPLOTA, VONKAJSIA VLHKOST
    // POSIELANIE KAZDU HODINU
    @Scheduled(fixedRate = 3600000) // 3,600,000 milliseconds = 1 hour
    @GetMapping("/send-data-weather")
    public ResponseEntity<String> getAndSendWeatherData() {
//        List<Device> devices = deviceRepository.findAll();
        HashMap<Long, Device> devices = DeviceService.getDevices();
        for(Device device: devices.values()) {
            if(device.getCity() != null) {
                try{
                    final String uri = "http://api.weatherapi.com/v1/current.json?key=3d4c9ec96dd4442cbcf121421230711&q=" + device.getCity() + "&aqi=no";
                    RestTemplate restTemplate = new RestTemplate();
                    String result = restTemplate.getForObject(uri, String.class);

                    // Extract required attributes from the JSON response
                    assert result != null;
                    String temp_string = result.substring(result.indexOf("\"temp_c\":") + 9, result.indexOf(",\"temp_f\""));
                    String humidity_string = result.substring(result.indexOf("\"humidity\":") + 11, result.indexOf(",\"cloud\""));

                    // Convert the extracted strings to integer values
                    float temp = Float.parseFloat(temp_string);
                    float humidity = Float.parseFloat(humidity_string);

                    // Create a JSON string with the extracted data
                    String jsonPayload = "{\"teplota_vonkajšia\":" + temp + ", \"vlhkosť_vonkajšia\":" + humidity + "}";

                    sendDataToThingsBoard(jsonPayload, device.getAccessToken());

                    log.info("Data was sent to the " + device.getName() + "(" + device.getOwner() + "): " + jsonPayload);

                } catch (Exception e) {
                    log.error("During generating outdoor temp and humidity an error occured: " + e);
                    return ResponseEntity.badRequest().body("During generating outdoor temp and humidity an error occured for beehive: " +
                            device.getName() + ", " + device.getOwner() + ", " + device.getCity());
                }

            }
        }
        log.info("Generating data for outdoor temp and humidity was successfully");
        return ResponseEntity.ok("Generating data for outdoor temp and humidity was successfully");

    }

    // VNUTORNA TEPLOTA, VNUTORNA VLHKOST, FREKVENCIA
    // POSIELANIE KAZDYCH 30 MIN
    @Scheduled(fixedRate = 1800000) // 1,800,000 milliseconds = 30 minutes
//    @GetMapping("/send-data-bee-hive")
    public ResponseEntity<String> generateAndSendBeeHiveTelemetry() {
        HashMap<Long, Device> devices = DeviceService.getDevices();
        for(Device device: devices.values()) {
            try {
                Random random = new Random();

                // Generate random float values for temperature and humidity within the specified ranges
                float temperature = 32.0f + random.nextFloat() * (37.0f - 32.0f);
                float humidity = 55.0f + random.nextFloat() * (75.0f - 55.0f);

                // Generate random float for frequency, based on current day
                LocalDate currentDate = LocalDate.now();
                int day = currentDate.getDayOfMonth();
                float frequency = 0.0f;
                switch(day) {
                    // Simulovanie straty matky (kralovnej)
                    case 5:
                        frequency = 250.0f + random.nextFloat() * (260.0f - 250.0f);
                        // Simulovanie rojenia vciel
                    case 10:
                        frequency = 500.0f + random.nextFloat() * (700.0f - 500.0f);
                        // Simulovanie prehrievanie ula
                    case 15:
                        frequency = 140.0f + random.nextFloat() * (170.0f - 140.0f);
                        // Simulovanie nedostatku mednych zasob
                    case 20:
                        frequency = 50.0f + random.nextFloat() * (80.0f - 50.0f);
                        // Defaultna frekvencia
                    default:
                        frequency = 260.0f + random.nextFloat() * (500.0f - 260.0f);
                }

                float temperature_rounded = (float) (Math.round(temperature * 10.0) / 10.0);
                float humidity_rounded = (float) (Math.round(humidity * 10.0) / 10.0);
                float frequency_rounded = (float) (Math.round(frequency * 10.0) / 10.0);


                // Create a JSON string with the generated data
                String jsonPayload = "{ \"teplota_vnútorná\":" + temperature_rounded + ", " +
                        "\"vlhkosť_vnútorná\":" + humidity_rounded + ", " + "\"frekvencia_Hz\":" + frequency_rounded + "}";

                sendDataToThingsBoard(jsonPayload, device.getAccessToken());

                LOGGER.info("Data was sent to the " + device.getName() + "(" + device.getOwner() + "): " + jsonPayload);
            } catch (Exception e) {
                log.error("During generating indoor temp, humidity and frequency an error occured: " + e);
                return ResponseEntity.badRequest().body("During generating indoor temp, humidity and frequency an error occured for beehive: " +
                        device.getName() + ", " + device.getOwner() + ", " + device.getCity());
            }
        }
        log.info("Generating data for indoor temp, humidity and frequency was successfully");
        return ResponseEntity.ok("Generating data for indoor temp, humidity and frequency was successfully");
    }


    // HMOTNOST, NAKLONENIE A BATERIA
    // POSIELANIE RAZ ZA DEN
    @Scheduled(fixedRate = 86400000) // 86,400,000 milliseconds = 24 hours
//    @GetMapping("/send-data-weight-rollover")
    public ResponseEntity<String> generateAndSendWeightRollover() {
        HashMap<Long, Device> devices = DeviceService.getDevices();
        if(WEIGHT >= 40) {
            WEIGHT = 15;
        }
        if(BATTERY <= 20) {
            BATTERY = 80;
        }
        for(Device device: devices.values()) {
            try {
                Random random = new Random();

//            float weight_rounded = (float) (Math.round(WEIGHT * 10.0) / 10.0);
//            int battery = BATTERY;

                boolean rollover = random.nextDouble() < 0.9;

                // Create a JSON string with the extracted data
                String jsonPayload = "{\"hmotnosť\":" + WEIGHT + ", \"prevrátenie\":" + rollover + ", \"batéria\":" + BATTERY + "}";

                sendDataToThingsBoard(jsonPayload, device.getAccessToken());

                log.info("Data was sent to the " + device.getName() + "(" + device.getOwner() + "): " + jsonPayload);
            } catch (Exception e) {
                log.error("During generating weight, rollover and battery an error occured: " + e);
                return ResponseEntity.badRequest().body("During generating weight, rollover and battery an error occured for beehive: " +
                        device.getName() + ", " + device.getOwner() + ", " + device.getCity());
            }
        }
        WEIGHT = WEIGHT + 3;
        BATTERY = BATTERY - 5;
        log.info("Generating data for weight, rollover and battery was successfully");
        return ResponseEntity.ok("Generating data for weight, rollover and battery was successfully");
    }

    public void sendDataToThingsBoard(String jsonPayload, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare the headers for the POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with headers and payload
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange("http://165.22.17.201:8080/api/v1/" + accessToken + "/telemetry",
                HttpMethod.POST, request, String.class);
    }

}