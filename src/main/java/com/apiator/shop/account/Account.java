package com.apiator.shop.account;

import com.apiator.shop.item.Item;
import com.apiator.shop.purchase.Purchase;
import com.apiator.shop.purchase.PurchaseKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @Type(type="uuid-char")
    private UUID id;
    private Role role;
    @OneToMany(mappedBy = "account")
    private List<Purchase> purchasedItems;

    public Account(UUID id) {
        this.id = id;
        role = Role.USER;
    }

    public static UUID getAccountUUIDFromJwt(Jwt jwt){
        Map<String, Object> claims = jwt.getClaims();
        return UUID.fromString((String) claims.get("sub"));
    }

    public void addPurchasedItem(Item item){
        Optional<Purchase> matchingPurchase = purchasedItems.stream()
                .filter(purchase -> purchase.getItem().getId() == item.getId())
                .findAny();
        if(matchingPurchase.isPresent()){
            int currentAmount = matchingPurchase.get().getAmount();
            matchingPurchase.get().setAmount(currentAmount + 1);
        }
        else{
            PurchaseKey newPurchaseKey = new PurchaseKey(id, item.getId());
            Purchase newPurchase = new Purchase(newPurchaseKey, this, item, 1);
            purchasedItems.add(newPurchase);
        }
    }
}
