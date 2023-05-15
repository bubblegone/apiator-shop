package com.apiator.shop.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private String sender;
    private String recipient;
    private int amount;
    private String paymentToken;
}
