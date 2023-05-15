package com.apiator.shop.purchase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseKey implements Serializable {
    @Column(name = "account_id")
    UUID accountId;
    @Column(name = "item_id")
    long itemId;
}
