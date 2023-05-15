package com.apiator.shop.payment;

import com.apiator.shop.JwtGenerator;
import com.apiator.shop.account.Account;
import com.apiator.shop.account.AccountService;
import com.apiator.shop.exception.ApiException;
import com.apiator.shop.item.Item;
import com.apiator.shop.item.ItemRepository;
import com.apiator.shop.item.ItemService;
import com.apiator.shop.purchase.Purchase;
import com.apiator.shop.redis.RedisClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.jwt.Jwt;


import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private RedisClient redisClient;
    @Mock
    private PaymentVerifierService paymentVerifierService;
    @Mock
    private ItemService itemService;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private PaymentService paymentService;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    ArgumentCaptor<Account> accountArgumentCaptor;


    @Test
    void createPaymentRequestFailOnItemOutOfStock(){
        long itemId = 5675L;
        Item item = new Item();
        item.setId(itemId);
        item.setCount(0);
        when(itemService.getItemById(itemId)).thenReturn(item);
        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            paymentService.createPaymentRequest(itemId);
        });
        assertEquals("Item is out of stock", thrownException.getMessage());
    }

    @Test
    void createPaymentRequest(){
        long itemId = 5675L;
        Item item = new Item();
        item.setId(itemId);
        item.setCount(2);
        when(itemService.getItemById(itemId)).thenReturn(item);
        paymentService.createPaymentRequest(itemId);
        verify(redisClient).set(any(), eq(String.valueOf(itemId)), eq(60 * 15L));
    }

    @Test
    void purchaseItm(){
        long paymentId = 7878L;
        long itemId = 9876L;
        String paymentToken = "token-test-value";

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentToken(paymentToken);

        Item item = new Item();
        item.setId(itemId);
        int initialCount = 3;
        item.setCount(initialCount);
        
        String sub = "ca9784f0-7195-4f8a-acfc-56366837a3a4";
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        Account currentUserAccount = new Account();
        currentUserAccount.setId(UUID.fromString(sub));
        currentUserAccount.setPurchasedItems(new ArrayList<>());

        Purchase purchase = new Purchase();
        purchase.setItem(item);
        purchase.setAmount(1);

        when(paymentVerifierService.getPaymentInfo(paymentId)).thenReturn(paymentInfo);
        when(redisClient.get(paymentToken)).thenReturn(String.valueOf(itemId));
        when(itemService.getItemById(item.getId())).thenReturn(item);
        when(accountService.getAccountByUUID(UUID.fromString(sub))).thenReturn(currentUserAccount);

        paymentService.purchaseItem(jwt, paymentId);

        verify(paymentVerifierService).getPaymentInfo(paymentId);
        verify(itemService).getItemById(itemId);
        verify(redisClient).del(paymentToken);
        verify(itemService).saveItem(itemArgumentCaptor.capture());
        verify(accountService).updateAccount(accountArgumentCaptor.capture());

        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals(savedItem.getId(), item.getId());
        assertEquals(initialCount - 1, savedItem.getCount());

        Account savedAccount = accountArgumentCaptor.getValue();
        Optional<Purchase> savedPurchaseOptional = savedAccount.getPurchasedItems().stream()
                .filter(purchase1 -> purchase1.getItem().getId() == itemId).findFirst();
        assertThat(savedPurchaseOptional).isPresent();
        Purchase savedPurchase = savedPurchaseOptional.get();
        assertEquals(itemId, savedPurchase.getItem().getId());
        assertEquals(UUID.fromString(sub), savedPurchase.getAccount().getId());
    }
}