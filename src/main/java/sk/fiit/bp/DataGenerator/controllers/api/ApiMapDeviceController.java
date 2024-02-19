package sk.fiit.bp.DataGenerator.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sk.fiit.bp.DataGenerator.model.database.Device;
import sk.fiit.bp.DataGenerator.model.mapDevice.*;
import sk.fiit.bp.DataGenerator.model.mapDevice.wrappers.AccessTokenWrapper;
import sk.fiit.bp.DataGenerator.model.mapDevice.wrappers.DevicesWrapper;
import sk.fiit.bp.DataGenerator.model.mapDevice.wrappers.TenantsWrapper;
import sk.fiit.bp.DataGenerator.model.mapDevice.wrappers.UsersWrapper;
import sk.fiit.bp.DataGenerator.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;


@RestController
@Component
public class ApiMapDeviceController {
    final RestTemplate restTemplate = new RestTemplate();
    final Logger logger = LoggerFactory.getLogger(ApiMapDeviceController.class);
    final String urlAdress = "http://165.22.17.201/";

    @Autowired
    DeviceRepository deviceRepository;

    @GetMapping("get-map-devices")
    public void getAllDeviceAndDashboards() {
        try {
            String adminToken = adminLogin();
            List<Device> allDevices = new ArrayList<>();
            if(adminToken != null) {
                List<Tenant> tenants = getAllTenants(adminToken);
                if(tenants != null) {
                    for(Tenant tenant: tenants) {
                        User user = getTenantOwner(tenant, adminToken);
                        String userToken = getUserToken(adminToken, user);
                        List<Device> devices = getUserPublicDevices(userToken, user);
                        if(devices != null && devices.size() > 0) {
                            allDevices.addAll(devices);
                        }
                    }
                }
            }
            if(allDevices.size() > 0) {
                deviceRepository.deleteAll();
                deviceRepository.saveAll(allDevices);
            }
            logger.info("Získanie monitorvaných včelíčh úľov prebehlo úspešne.");
        } catch (Exception e) {
            logger.warn("Pri získavaní monitorovaných včelích úľov vznikla chyba vznikla chyba: ", e);
        }
    }

    /**
     * Metóda, ktorá simuluje prihlásenie admina do platformy ThingsBoard.
     * Vracia autorizačný token, vďaka ktorému môžeme ďalej volať API requesty ako administrátor.
     */
    public String adminLogin() {
        String url = urlAdress + "api/auth/login";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // string pre username a password
        String requestBody = "{\"username\": \"adam4grik@gmail.com\", \"password\": \"rFhLZ6S8Pn7UFeL\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

        // Send the POST request
        ResponseEntity<Token> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Token.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            Token responseBody = responseEntity.getBody();
            assert responseBody != null;
            return responseBody.getToken();
        } else {
            return null;
        }
    }

    /**
     * Metóda vracia všetkých Tenantov (používateľov, včelárov), ktorý sú registrovaní
     * v našej platforme thinsboard.
     */
    public List<Tenant> getAllTenants(String adminToken) {
        String url = urlAdress + "api/tenants?pageSize=100&page=0";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        httpHeaders.set("X-Authorization", "Bearer " + adminToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<TenantsWrapper> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                TenantsWrapper.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            TenantsWrapper responseBody = responseEntity.getBody();
            assert responseBody != null;
            return responseBody.getData();
        } else {
            System.err.println("Request failed with status code: " + statusCode); // TESTING
            return null;
        }
    }

    /**
     * Metóda, ktorá vracia jedného usera vrámci Tenanta.
     * Je to buď user, ktorý je v platforme definovaný ako owner,
     * a keď nie je definovaný žiadny owner, tak berieme prvého usera, ktorý sa nachádza v danom Tenantovi.
     */
    public User getTenantOwner(Tenant tenant, String adminToken) {
        String url = urlAdress + "api/tenant/" + tenant.getId().getId() + "/users?pageSize=100&page=0";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        httpHeaders.set("X-Authorization", "Bearer " + adminToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<UsersWrapper> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                UsersWrapper.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            UsersWrapper tenantUsers = responseEntity.getBody();

            assert tenantUsers != null;
            if(tenantUsers.getData() != null && tenantUsers.getData().size() > 0) {
                for(User user: tenantUsers.getData()) {
                    if(user.getAdditionalInfo().getDescription().equals("owner")) {
                        return user;
                    }
                }
                return tenantUsers.getData().get(0);
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * Metóda, ktorá vracia autorizačný token pre daného usera.
     * Aby sme vedeli získať jeho devices.
     */
    public String getUserToken(String adminToken, User user) {
        String url = urlAdress + "api/user/"+ user.getId().getId() + "/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-Authorization", "Bearer " + adminToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Token> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Token.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            Token responseBody = responseEntity.getBody();
            assert responseBody != null;
            return responseBody.getToken();
        } else {
            return null;
        }
    }

    /**
     Metóda pre získanie všetkých public devices, pre daného usera.
     */
    public List<Device> getUserPublicDevices(String userToken, User user) {
        List<Device> devices = new ArrayList<>();
        String url = urlAdress + "api/tenant/devices?pageSize=100&page=0";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-Authorization", "Bearer " + userToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<DevicesWrapper> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                DevicesWrapper.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            DevicesWrapper userDevices = responseEntity.getBody();
            assert userDevices != null;
            if (userDevices.getData() != null && userDevices.getData().size() > 0) {
                for (DeviceTemp deviceTemp : userDevices.getData()) {
                    if (deviceTemp.getLabel() != null && !deviceTemp.getLabel().isEmpty() && deviceTemp.getLabel().equals("public")) {
                        Device device = getDeviceAttributes(userToken, deviceTemp.getId().getId());
                        if(device != null) {
                            device.setName(deviceTemp.getName());
                            device.setOwner(user.getFirstName() + " " + user.getLastName());
                            devices.add(device);
                        }
                    }
                }
            }
            return devices;
        } else {
            return null;
        }
    }

    public Device getDeviceAttributes(String userToken, String deviceId) {
        String url = urlAdress + "api/plugins/telemetry/DEVICE/" + deviceId + "/values/attributes";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-Authorization", "Bearer " + userToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ParameterizedTypeReference<List<Attribute>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Attribute>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                responseType
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            List<Attribute> attributes = responseEntity.getBody();
            boolean hasDashboard = false;
            boolean hasLatitude = false;
            boolean hasLongitude = false;
            boolean hasCity = false;
            Attribute dashboardAttribute = new Attribute();
            Attribute latitudeAttribute = new Attribute();
            Attribute longitudeAttribute = new Attribute();
            Attribute cityAttribute = new Attribute();

            if(attributes != null && attributes.size() > 0) {
                for (Attribute attribute : attributes) {
                    if ("dashboard".equals(attribute.getKey()) && attribute.getValue() != null) {
                        hasDashboard = true;
                        dashboardAttribute.setValue(attribute.getValue());
                    }
                    if ("latitude".equals(attribute.getKey()) && attribute.getValue() != null) {
                        String latitude = attribute.getValue().toString();
                        if (latitude.matches("^-?\\d+(\\.\\d+)?$")) {
                            hasLatitude = true;
                            latitudeAttribute.setValue(attribute.getValue());
                        }
                    }
                    if ("longitude".equals(attribute.getKey()) && attribute.getValue() != null) {
                        String longitude = attribute.getValue().toString();
                        if (longitude.matches("^-?\\d+(\\.\\d+)?$")) {
                            hasLongitude = true;
                            longitudeAttribute.setValue(attribute.getValue());
                        }
                    }
                    if("city".equals(attribute.getKey()) && attribute.getValue() != null) {
                        hasCity = true;
                        cityAttribute.setValue(attribute.getValue());
                    }
                }
            }

            String deviceAccessToken = getDeviceAccessToken(userToken, deviceId);

            Device device = new Device();
            if(hasLatitude && hasLongitude) {
                device.setLatitude((Double) latitudeAttribute.getValue());
                device.setLongitude((Double) longitudeAttribute.getValue());
            }
            if(hasDashboard) {
                device.setDashboardLink((String) dashboardAttribute.getValue());
            }
            if(hasCity) {
                device.setCity((String) cityAttribute.getValue());
            }
            if(deviceAccessToken != null) {
                device.setAccessToken(deviceAccessToken);
            }
            return device;
        }
        return null;
    }

    public String getDeviceAccessToken(String userToken, String deviceId) {
        String url = "http://165.22.17.201:80/api/device/" + deviceId + "/credentials";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-Authorization", "Bearer " + userToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<AccessTokenWrapper> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                AccessTokenWrapper.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            AccessTokenWrapper responseBody = responseEntity.getBody();
            assert responseBody != null;
            return responseBody.getCredentialsId();
        } else {
            return null;
        }
    }
}
