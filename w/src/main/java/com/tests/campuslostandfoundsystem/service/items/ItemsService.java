package com.tests.campuslostandfoundsystem.service.items;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.entity.admins.ItemTypeCountDTO;
import com.tests.campuslostandfoundsystem.entity.items.ItemsSelectionDTO;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ItemsService extends IService<Items>{
    Page<Items> getAllItemsPages(ItemsSelectionDTO dto);
    void insertItem(Items items);
    void updateItem(Items items);
    void deleteItem(Long id);
    ItemTypeCountDTO getItemTypeCount();
}
