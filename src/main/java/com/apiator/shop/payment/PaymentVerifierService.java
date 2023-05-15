package com.apiator.shop.payment;

import com.apiator.shop.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class PaymentVerifierService {
    private final WebClient client;

    public PaymentVerifierService(@Value("${app.payment-check-url}") String requestUrl) {
        client = WebClient.builder()
                .baseUrl(requestUrl)
                .build();
    }

    public PaymentInfo getPaymentInfo(long paymentId) {
        WebClient.ResponseSpec responseSpec = client.get()
                .uri(String.valueOf(paymentId))
                .retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        (ClientResponse response ) -> Mono.error(
                                new ApiException("Could not get payment info with provided payment id",
                                        HttpStatus.NOT_FOUND)
                        )
                    );
        ResponseEntity<PaymentInfo> paymentInfoResponseEntity = responseSpec.toEntity(PaymentInfo.class).block();

        return Objects.requireNonNull(paymentInfoResponseEntity, "Response body is empty").getBody();
    }
}
