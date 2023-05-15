package com.apiator.shop.purchase;

import com.apiator.shop.account.Account;
import com.apiator.shop.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @EmbeddedId
    PurchaseKey id;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    Account account;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    Item item;

    int amount;
}
