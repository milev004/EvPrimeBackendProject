package evprimeTests;

import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.SignUpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import static objectBuilder.SignUpBuilder.createBodyForSIgnUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SignUpTests {


    @Test
    public void successfulSignUpTest() {
        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        Response response = new EVPrimeClient().signUp(signUpRequest);
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals("User created.", signUpResponse.getMessage());
        assertNotNull(signUpResponse.getUser().getId());
        assertEquals(signUpRequest.getEmail(), signUpResponse.getUser().getEmail());
        assertNotNull(signUpResponse.getToken());
    }

    @Test
    public void emailAlreadyExistTest() {

        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail("EVaTL4AZfs@mail.com")
                .setPassword("YePqbMzLHC")
                .createRequest();
        Response response = new EVPrimeClient().signUp(signUpRequest);
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Email exists already.", signUpResponse.getErrors().getEmail());
    }

    @Test
    public void wrongEmailFormatTest() {
        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail("8VyNCA8cta.mail.com")
                .setPassword("hBtwV770jP")
                .createRequest();
        Response response = new EVPrimeClient().signUp(signUpRequest);
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Invalid email.", signUpResponse.getErrors().getEmail());
    }

    @Test
    public void passwordHasLessThanSixCharTest() {
        SignUpLoginRequest signUpRequest = new SignUpLoginDataFactory(createBodyForSIgnUp())
                .setEmail("8VyNCA8cta@mail.com")
                .setPassword("hBtw")
                .createRequest();
        Response response = new EVPrimeClient().signUp(signUpRequest);
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Invalid password. Must be at least 6 characters long.", signUpResponse.getErrors().getPassword());
    }

}
