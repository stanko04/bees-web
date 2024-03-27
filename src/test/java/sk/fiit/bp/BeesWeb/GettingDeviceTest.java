package sk.fiit.bp.BeesWeb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sk.fiit.bp.BeesWeb.controllers.api.ApiMapDeviceController;
import sk.fiit.bp.BeesWeb.model.mapDevice.Id;
import sk.fiit.bp.BeesWeb.model.mapDevice.Tenant;
import sk.fiit.bp.BeesWeb.model.mapDevice.Token;
import sk.fiit.bp.BeesWeb.model.mapDevice.User;
import sk.fiit.bp.BeesWeb.model.mapDevice.wrappers.TenantsWrapper;
import sk.fiit.bp.BeesWeb.model.mapDevice.wrappers.UsersWrapper;

import javax.security.auth.login.LoginException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GettingDeviceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    ApiMapDeviceController apiMapDeviceController;

    private final String apiUrl = "http://165.22.17.201";

    @Test
    public void adminLogin_unauthorizedRequest() {
        String adminUsername = "admin@admin.com";
        String adminPassword = "password";

        ReflectionTestUtils.setField(apiMapDeviceController, "adminUsername", adminUsername);
        ReflectionTestUtils.setField(apiMapDeviceController, "adminPassword", adminPassword);

        String url = apiUrl + "/api/auth/login";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Token.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class, () -> apiMapDeviceController.adminLogin(),
                "Expected adminLogin() to throw, but it didn't");
    }

    @Test
    public void getAllTenants_unauthorizedAdminToken() {
        String adminToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=" +
                "/CHyL?jA!AcxF6FYvu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZ" +
                "qrHa0qpsZcJ2qhU1jpzTHTE=F9w=gU6bVKdFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHn" +
                "Pbl1Op1/X!!cIpquP?";
        String url = apiUrl + "/api/tenants?pageSize=100&page=0";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(TenantsWrapper.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getAllTenants(adminToken),
                "Expected getAllTenants() to throw LoginException due to incorrect admin token, but it didn't");
    }

    @Test
    public void getTenantOwner_unauthorizedAdminToken() {
        Tenant tenant = new Tenant();
        Id id = new Id();
        id.setId("275d5770-7e31-11ee-b773-f77898e09839");
        tenant.setId(id);
        String incorrectAdminToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=/CHyL?jA" +
                "!AcxF6FYvu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZqrHa0qpsZcJ2qhU1jpzTHTE=F" +
                "9w=gU6bVKdFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHnPbl1Op1/X!!cIpquP?";
        String url = apiUrl + "/api/tenant/" + tenant.getId().getId() + "/users?pageSize=100&page=0";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UsersWrapper.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getTenantOwner(tenant, incorrectAdminToken),
                "Expected getTenantOwner() to throw LoginException due to incorrect admin token, but it didn't");
    }

    @Test
    public void getUserToken_unauthorizedAdminToken() {
        String adminToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=/CHyL?jA!AcxF6FY" +
                "vu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZqrHa0qpsZcJ2qhU1jpzTHTE=F9w=gU6bVK" +
                "dFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHnPbl1Op1/X!!cIpquP?";
        User testUser = new User();
        Id id = new Id();
        id.setId("275d5770-7e31-11ee-b773-f77898e09839");
        testUser.setId(id);

        String url = apiUrl+ "/api/user/" + testUser.getId().getId() + "/token";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Token.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getUserToken(adminToken, testUser),
                "Expected getUserToken() to throw LoginException due to incorrect admin token, but it didn't");
    }

    @Test
    public void getUserPublicDevices_unauthorizedUserToken() {
        String userToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=/CHyL?jA!AcxF6FY" +
                "vu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZqrHa0qpsZcJ2qhU1jpzTHTE=F9w=gU6bVK" +
                "dFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHnPbl1Op1/X!!cIpquP?";

        User testUser = new User();
        Id id = new Id();
        id.setId("275d5770-7e31-11ee-b773-f77898e09839");
        testUser.setId(id);

        String url = apiUrl + "api/tenant/devices?pageSize=100&page=0";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Token.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getUserPublicDevices(userToken, testUser),
                "Expected getUserPublicDevices to throw LoginException due to incorrect user token, but it didn't");
    }

    @Test
    public void getDeviceAttributes_unauthorizedUserToken() {
        String userToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=/CHyL?jA!AcxF6FY" +
                "vu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZqrHa0qpsZcJ2qhU1jpzTHTE=F9w=gU6bVK" +
                "dFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHnPbl1Op1/X!!cIpquP?";
        String deviceId = "275d5770-7e31-11ee-b773-f77898e09839";

        String url = apiUrl + "api/plugins/telemetry/DEVICE/" + deviceId + "/values/attributes";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Token.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getDeviceAttributes(userToken, deviceId),
                "Expected getDeviceAttributes to throw LoginException due to incorrect user token, but it didn't");
    }

    @Test
    public void getDeviceAccessToken_unauthorizedUserToken() {
        String userToken = "Q?TcrAMyeo9aGpRr9H=BK=3LwK?k9S6AweBme60rt!NbZTYrGGsXNeIzF08-RKO66T5D=/CHyL?jA!AcxF6FY" +
                "vu09MGgcB00leTOhPgaWG?ir!p=Eq7MU2IeNeAIIq4zqtfhFnr87aD8pofsCVmoJCeGZqrHa0qpsZcJ2qhU1jpzTHTE=F9w=gU6bVK" +
                "dFfkTa3YRqzWmAVB7IprwskjQ/w1WX3QEyd9o0j/re51A0mlYHnPbl1Op1/X!!cIpquP?";
        String deviceId = "275d5770-7e31-11ee-b773-f77898e09839";

        String url = "http://165.22.17.201:80/api/device/" + deviceId + "/credentials";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Token.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(LoginException.class,
                () -> apiMapDeviceController.getDeviceAccessToken(userToken, deviceId),
                "Expected getDeviceAccessToken to throw LoginException due to incorrect user token, but it didn't");
    }

}









