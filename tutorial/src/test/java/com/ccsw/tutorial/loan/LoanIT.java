package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.HashMap;
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
    private static final int EXIST_LOAN_ID = 2;
    private static final String EXISTING_DATE = "2024-05-29";
    private static final String EXISTING_GAME_TITLE = "Aventureros al tren";

    private static final int PAGE_SZE = 2;
    private static final int TOTAL_LOANS = 4;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    @Test
    public void findFirstPageWithTwoSizeShouldReturnFirstTwoResults() {
        LoanSearchDto loanSearchDto = new LoanSearchDto();
        loanSearchDto.setPageable(new PageableRequest(0, PAGE_SZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(loanSearchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageShouldReturnTwoResults() {
        int loanCount = TOTAL_LOANS - PAGE_SZE;

        LoanSearchDto loanSearchDto = new LoanSearchDto();
        loanSearchDto.setPageable(new PageableRequest(1, PAGE_SZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(loanSearchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(loanCount, response.getBody().getContent().size());
    }

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {

        long reultLoanSize = TOTAL_LOANS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + EXIST_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        LoanSearchDto loanSearchDto = new LoanSearchDto();
        loanSearchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(loanSearchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(reultLoanSize, response.getBody().getTotalElements());
    }

    private String getUrlWithParams() {
        return UriComponentsBuilder.fromHttpUrl(LOCALHOST + port + SERVICE_PATH).queryParam(GAME_TITLE_PARAM, "{" + GAME_TITLE_PARAM + "}").queryParam(CLIENT_NAME_PARAM, "{" + CLIENT_NAME_PARAM + "}")
                .queryParam(DATE_PARAM, "{" + DATE_PARAM + "}").encode().toUriString();
    }

    @Test
    public void findWithExistFilterShouldReturnLoan() {
        int FILTER_BY_DATE = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(DATE_PARAM, EXISTING_DATE);
        params.put(GAME_TITLE_PARAM, null);
        params.put(CLIENT_NAME_PARAM, null);

        LoanSearchDto loanSearchDto = new LoanSearchDto();
        loanSearchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(loanSearchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(FILTER_BY_DATE, response.getBody().getTotalElements());
    }

    @Test
    public void findWithTwoFiltersSouldReturnLoan() {
        int FILTER_BY_DATE_AND_GAME = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(DATE_PARAM, EXISTING_DATE);
        params.put(GAME_TITLE_PARAM, EXISTING_GAME_TITLE);
        params.put(CLIENT_NAME_PARAM, null);

        LoanSearchDto loanSearchDto = new LoanSearchDto();
        loanSearchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(loanSearchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(FILTER_BY_DATE_AND_GAME, response.getBody().getTotalElements());
    }

    @Test
    public void saveWithExistingLoanedGameShouldTrowLoanValidationException() {
        LoanDto dto = new LoanDto();
        GameDto gameDto = new GameDto();
        gameDto.setId(2L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(5L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setLoan_date(new Date(2024, 05, 28));
        dto.setReturn_date(new Date(2024, 06, 10));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }
}
