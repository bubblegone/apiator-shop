package com.apiator.shop.payment;

import com.apiator.shop.account.Account;
import com.apiator.shop.account.AccountService;
import com.apiator.shop.exception.ApiException;
import com.apiator.shop.item.Item;
import com.apiator.shop.item.ItemService;
import com.apiator.shop.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final RedisClient redisClient;
    private final PaymentVerifierService paymentVerifierService;
    private final ItemService itemService;
    private final AccountService accountService;

    @Autowired
    public PaymentService(PaymentVerifierService paymentVerifierService, RedisClient redisClient,
                          ItemService itemService, AccountService accountService) {
        this.redisClient = redisClient;
        this.paymentVerifierService = paymentVerifierService;
        this.itemService = itemService;
        this.accountService = accountService;
    }

    public String createPaymentRequest(long itemId){
        String token = UUID.randomUUID().toString();
        Item item = itemService.getItemById(itemId);
        if(item.getCount() == 0){
            throw new ApiException("Item is out of stock", HttpStatus.BAD_REQUEST);
        }
        redisClient.set(token, String.valueOf(itemId),60 * 15);
        return token;
    }

    public void purchaseItem(Jwt currentUserJwt, long paymentId){
        PaymentInfo paymentInfo = paymentVerifierService.getPaymentInfo(paymentId);
        String paymentToken = paymentInfo.getPaymentToken();
        String itemIdString = redisClient.get(paymentToken);

        long itemId = Long.parseLong(itemIdString);
        Item item = itemService.getItemById(itemId);

        UUID currentUserUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        Account currentUser = accountService.getAccountByUUID(currentUserUUID);

        currentUser.addPurchasedItem(item);

        item.setCount(item.getCount() - 1);
        redisClient.del(paymentToken);
        itemService.saveItem(item);
        accountService.updateAccount(currentUser);
    }
}
