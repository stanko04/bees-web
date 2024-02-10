package sk.fiit.bp.DataGenerator.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sk.fiit.bp.DataGenerator.model.beeHive.Device;
import sk.fiit.bp.DataGenerator.model.beeHive.Token;
import sk.fiit.bp.DataGenerator.model.beeHive.Tenant;
import sk.fiit.bp.DataGenerator.model.beeHive.User;
import sk.fiit.bp.DataGenerator.model.beeHive.wrappers.DevicesWrapper;
import sk.fiit.bp.DataGenerator.model.beeHive.wrappers.TenantsWrapper;
import sk.fiit.bp.DataGenerator.model.beeHive.wrappers.UsersWrapper;

import java.util.List;


@RestController
@Component
public class BeeHiveController {
    final RestTemplate restTemplate = new RestTemplate();
    final Logger logger = LoggerFactory.getLogger(BeeHiveController.class);
    final String urlAdress = "http://165.22.17.201/";

    @GetMapping("get-tenants")
    public void getAllBeeHivesAndDashboards() {
        try {
            String adminToken = adminLogin();

            if(adminToken != null) {
                List<Tenant> tenants = getAllTenants(adminToken);
                if(tenants != null) {
                    for(Tenant tenant: tenants) {
                        User user = getTenantOwner(tenant, adminToken);
                        System.out.println(user);
                        String userToken = getUserToken(adminToken, user);
                        getUserDevices(userToken);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Pri ziskavani monitorovanych ulov vznikla chyba: ", e);
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
            for(User user: tenantUsers.getData()) {
                if(user.getAdditionalInfo().getDescription().equals("owner")) {
                    return user;
                }
            }
            return tenantUsers.getData().get(0);
        } else {
            return null;
        }
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
    public void getUserDevices(String userToken) {
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
            if(userDevices.getData().size() > 0) {
//                System.out.println(userDevices.getData());
                // TODO - spravit tu for loop, ktory prejde vsetky devices, ktore maju lablel "public"
                for(Device device: userDevices.getData()) {
                    System.out.println(device);
                }
                // TODO - nasledne sa vo for loope napajat na api, kde ziskame attributes pre kazde device
                // TODO - tieto vsetky devices aj s nazvom, ownerom a atributmi vratime na returne
            }
        } else {
//            return null;
        }
    }
}
