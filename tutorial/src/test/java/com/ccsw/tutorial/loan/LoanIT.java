package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";
    private static final String GAME_TITLE_PARAM = "titleGame";
    private static final String CLIENT_NAME_PARAM = "clientName";
    private static final String DATE_PARAM = "date";
    private static final Date EXISTING_DATE = new Date(2024, 05, 29);
    private static final String EXISTING_GAME_TITLE = "Aventureros al tren";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<LoanDto>> responseType = new ParameterizedTypeReference<List<LoanDto>>() {
    };

    private String getUrlWithParams() {
        return UriComponentsBuilder.fromHttpUrl(LOCALHOST + port + SERVICE_PATH).queryParam(GAME_TITLE_PARAM, "{" + GAME_TITLE_PARAM + "}").queryParam(CLIENT_NAME_PARAM, "{" + CLIENT_NAME_PARAM + "}")
                .queryParam(DATE_PARAM, "{" + DATE_PARAM + "}").encode().toUriString();
    }

    @Test
    public void findExistsTitleShouldReturnLoan() {

        int LOANS_WITH_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_TITLE_PARAM, EXISTING_GAME_TITLE);
        params.put(CLIENT_NAME_PARAM, null);
        params.put(DATE_PARAM, null);

        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(LOANS_WITH_FILTER, response.getBody().size());
    }

}
