package com.tests.campuslostandfoundsystem.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.entity.R;
import com.tests.campuslostandfoundsystem.entity.admins.ItemTypeCountDTO;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.tests.campuslostandfoundsystem.entity.items.ItemsSelectionDTO;
import com.tests.campuslostandfoundsystem.service.items.ItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
* 失物信息表(items)表控制层
*
* @author xxxxx
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemsController {
   private final ItemsService itemsService;

    @GetMapping("/")
    public R<Page<Items>> getAllItemsPages(ItemsSelectionDTO dto){
        return R.success(itemsService.getAllItemsPages(dto));
    }
    @PostMapping("/")
    public R<Void> insertItem(Items items){
        itemsService.insertItem(items);
        return R.success(null);
    }
    @PutMapping("/")
    public R<Void> updateItem(Items items){
        itemsService.updateItem(items);
        return R.success(null);
    }
    @DeleteMapping("/{id}")
    public R<Void> deleteItem( @PathVariable("id") Long id){
        itemsService.deleteItem(id);
        return R.success(null);
    }
    @GetMapping("/itemTypeCount")
    public  R<ItemTypeCountDTO> getItemTypeCount(){
        return R.success(itemsService.getItemTypeCount());
    }

}
