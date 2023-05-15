package com.apiator.shop.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/item")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public void addItem(@RequestBody Item item){
        itemService.saveItem(item);
    }

    @PostMapping("/{id}")
    public void addItemImage(@RequestParam("file") MultipartFile file, @PathVariable(name = "id") long itemId){
        itemService.addImageToItem(file, itemId);
    }

}
