package com.apiator.shop.payment;

import com.apiator.shop.exception.ApiException;
import com.apiator.shop.exception.InternalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PaymentVerifierServiceTest {
    public static MockWebServer mockBackEnd;
    private PaymentVerifierService paymentVerifierService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        paymentVerifierService = new PaymentVerifierService(baseUrl);
    }

    @Test
    void getPaymentInfo() throws Exception{
        PaymentInfo mockPaymentInfo = new PaymentInfo("1234", "4321", 100, "pay-tk");
        ObjectMapper objectMapper = new ObjectMapper();
        mockBackEnd.enqueue(
                new MockResponse()
                        .setBody(objectMapper.writeValueAsString(mockPaymentInfo))
                        .addHeader("Content-Type", "application/json")
        );
        PaymentInfo responsePaymentInfo = paymentVerifierService.getPaymentInfo(123L);
        assertEquals(responsePaymentInfo.getPaymentToken(), mockPaymentInfo.getPaymentToken());
        assertEquals(responsePaymentInfo.getAmount(), mockPaymentInfo.getAmount());
        assertEquals(responsePaymentInfo.getRecipient(), mockPaymentInfo.getRecipient());
        assertEquals(responsePaymentInfo.getSender(), mockPaymentInfo.getSender());
    }

    @Test
    void getPaymentInfoOnNoPaymentInfoFound() throws Exception{
        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(404)
        );
        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            paymentVerifierService.getPaymentInfo(567L);
        });
        assertEquals("Could not get payment info with provided payment id", thrownException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrownException.getHttpStatus());
    }
}