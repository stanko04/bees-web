package sk.fiit.bp.BeesWeb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.fiit.bp.BeesWeb.controllers.api.ApiGenerateController;
import sk.fiit.bp.BeesWeb.model.database.Device;
import sk.fiit.bp.BeesWeb.service.DeviceService;

import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataGeneratorTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ApiGenerateController apiGenerateController;

    @Test
    public void testOutdoorTempAndHumidity_invalidCity() {
        Device invalidTestingDevice = new Device(1L, "InvalidCity", "Test Owner", "Test Beehive", "Test Token");
        DeviceService.updateHashMap(Collections.singletonList(invalidTestingDevice));

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Failed to fetch weather data"));

        // Spustenie metody
        ResponseEntity<String> response = apiGenerateController.getAndSendWeatherData();

        // Kontrola ci je navratovy kod 400 a ci je spravne vrateny error message
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("During generating outdoor temp and humidity an error occured for beehive: " +
                invalidTestingDevice.getName() + ", " + invalidTestingDevice.getOwner() + ", " + invalidTestingDevice.getCity()));
    }

    @Test
    public void testAllGenerating_invalidAccessToken() {
        Device invalidTestingDevice = new Device(1L, "Trnava", "Test Owner", "Test Beehive", "TestToken");
        DeviceService.updateHashMap(Collections.singletonList(invalidTestingDevice));

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Failed to fetch weather data"));

        // Test pre generovanie vramci generovania vonkajsej teploty a vlhkost
        ResponseEntity<String> responseForOutdoorTempAndHumidity = apiGenerateController.getAndSendWeatherData();

        assertEquals(HttpStatus.BAD_REQUEST, responseForOutdoorTempAndHumidity.getStatusCode());
        assertTrue(Objects.requireNonNull(responseForOutdoorTempAndHumidity.getBody()).contains("During generating outdoor temp and humidity an error occured for beehive: " +
                invalidTestingDevice.getName() + ", " + invalidTestingDevice.getOwner() + ", " + invalidTestingDevice.getCity()));

        // Test pre generovanie vramci generovania vnutornej teploty, vlhkosti a frekvencie
        ResponseEntity<String> responseForIndoorTempHumidityAndFrequency = apiGenerateController.generateAndSendBeeHiveTelemetry();
        assertEquals(HttpStatus.BAD_REQUEST, responseForIndoorTempHumidityAndFrequency.getStatusCode());
        assertTrue(Objects.requireNonNull(responseForIndoorTempHumidityAndFrequency.getBody()).contains("During generating indoor temp, humidity and frequency an error occured for beehive: " +
                invalidTestingDevice.getName() + ", " + invalidTestingDevice.getOwner() + ", " + invalidTestingDevice.getCity()));

        // Test pre generovanie vramci generovania hmotnosti, prevratenia a baterie
        ResponseEntity<String> responseForWeightRolloverAndBattery = apiGenerateController.generateAndSendWeightRollover();
        assertEquals(HttpStatus.BAD_REQUEST, responseForWeightRolloverAndBattery.getStatusCode());
        assertTrue(Objects.requireNonNull(responseForWeightRolloverAndBattery.getBody()).contains("During generating weight, rollover and battery an error occured for beehive: " +
                invalidTestingDevice.getName() + ", " + invalidTestingDevice.getOwner() + ", " + invalidTestingDevice.getCity()));
    }






}
