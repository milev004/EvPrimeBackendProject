package evprimeTests;

import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.LoginErrorMessage;
import models.response.LoginResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DateBuilder;

import static objectBuilder.SignUpBuilder.createBodyForSIgnUp;
import static org.junit.Assert.*;

public class LoginTests {
  static   SignUpLoginRequest signUpLoginRequest;
   static DateBuilder dateBuilder = new DateBuilder();
    @BeforeClass
    public static void userSetUp(){
        signUpLoginRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail(RandomStringUtils.randomAlphanumeric(10)+ "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();
        new EVPrimeClient().signUp(signUpLoginRequest);

    }

    @Test
    public void successfulLogIn(){
        Response response = new EVPrimeClient()
                .login(signUpLoginRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(200,response.statusCode());
        assertNotNull(loginResponse.getToken());
        assertTrue(loginResponse.getExpirationTime().contains(dateBuilder.currentTimeMinusOneHour()));
    }

    @Test
    public void unsuccessfulLoginWrongEmailTest(){
        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail("pZzZ3Adp0c.mail.com")
                .setPassword("JTOqQqKLiz")
                .createRequest();
        Response response = new EVPrimeClient()
                .login(signUpRequest);
        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(401,response.statusCode());
        assertEquals("Authentication failed.",loginResponse.getMessage());

    }
    @Test
    public void unsuccessfulLoginWrongPasswordTest(){
        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail("1FaYrXSROD@mail.com")
                .setPassword("JTOqQqKLizggggg")
                .createRequest();
        Response response = new EVPrimeClient()
                .login(signUpRequest);
        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(422,response.statusCode());
        assertEquals("Invalid credentials.",loginResponse.getMessage());
        assertEquals("Invalid email or password entered.", loginResponse.getErrors().getCredentials());

    }
}

