package neoflex.statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.statement.controller.StatementController;
import neoflex.statement.service.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatementControllerTest {

    @Mock
    private StatementService statementService;

    @InjectMocks
    private StatementController statementController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(statementController).build();
    }

    @Test
    public void testGetLoanOffers() throws Exception {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanOfferDto offerDto1 = new LoanOfferDto();
        LoanOfferDto offerDto2 = new LoanOfferDto();
        List<LoanOfferDto> loanOffers = Arrays.asList(offerDto1, offerDto2);

        when(statementService.getLoanOffers(any(LoanStatementRequestDto.class))).thenReturn(loanOffers);

        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testSelectLoanOffer() throws Exception {
        LoanOfferDto offerDto = new LoanOfferDto();

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(offerDto)))
                .andExpect(status().isOk());
    }



    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
