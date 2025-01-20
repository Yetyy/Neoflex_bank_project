package neoflex.statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.statement.controller.StatementController;
import neoflex.statement.service.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StatementControllerTest {

    @Mock
    private StatementService statementService;

    @InjectMocks
    private StatementController statementController;

    private MockMvc mockMvc;

    private LoanStatementRequestDto requestDto;
    private LoanOfferDto offerDto1;
    private LoanOfferDto offerDto2;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statementController).build();
        requestDto = new LoanStatementRequestDto();
        offerDto1 = new LoanOfferDto();
        offerDto2 = new LoanOfferDto();
    }

    @Test
    public void testGetLoanOffers() throws Exception {
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
        doNothing().when(statementService).selectLoanOffer(any(LoanOfferDto.class));

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(offerDto1)))
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
