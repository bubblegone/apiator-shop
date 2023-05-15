package com.apiator.shop.item;

import com.apiator.shop.exception.ApiException;
import com.apiator.shop.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final StorageService storageService;

    @Autowired
    public ItemService(ItemRepository itemRepository, StorageService storageService) {
        this.itemRepository = itemRepository;
        this.storageService = storageService;
    }

    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public void addImageToItem(MultipartFile file, long itemId){
        Item item = itemRepository.getItemById(itemId).orElseThrow(
                () -> new ApiException("No such item", HttpStatus.NOT_FOUND)
        );
        String imageName = storageService.store(file, "images");
        item.setImageName(imageName);
        itemRepository.save(item);
    }

    public Item getItemById(long id){
        return itemRepository.getItemById(id).orElseThrow(
                () -> new ApiException("No such item", HttpStatus.NOT_FOUND)
        );
    }
}
