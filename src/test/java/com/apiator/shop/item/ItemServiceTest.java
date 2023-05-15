package com.apiator.shop.item;

import com.apiator.shop.exception.ApiException;
import com.apiator.shop.storage.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StorageService storageService;
    @InjectMocks
    private ItemService itemService;
    @Captor
    ArgumentCaptor<Item> argumentCaptor;

    @Test
    void addNewItem(){
        Item item = new Item();
        itemService.saveItem(item);
        verify(itemRepository).save(item);
    }

    @Test
    void addImageToItemFailOnNoSuchItem(){
        long itemId = 123L;
        byte[] content = "Filed content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("test_fail_file.jpg", content);
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.empty());
        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            itemService.addImageToItem(multipartFile, itemId);
        });
        assertEquals(thrownException.getMessage(), "No such item");
        assertEquals(thrownException.getHttpStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void addImageToItem(){
        Item item = new Item();
        item.setId(454L);
        byte[] content = "Content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("test_success_file.jpg", content);
        String returnString = "test-value-here";
        when(itemRepository.getItemById(item.getId())).thenReturn(Optional.of(item));
        when(storageService.store(multipartFile, "images")).thenReturn(returnString);
        itemService.addImageToItem(multipartFile, item.getId());
        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();
        assertEquals(savedItem.getImageName(), returnString);
    }

    @Test
    void getItemByIdFailOnNoSuchItem(){
        long itemId = 4756L;
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.empty());
        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            itemService.getItemById(itemId);
        });
        assertEquals(thrownException.getMessage(), "No such item");
    }
}