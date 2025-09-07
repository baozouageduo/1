package com.tests.campuslostandfoundsystem.service.items;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.entity.admins.ItemTypeCountDTO;
import com.tests.campuslostandfoundsystem.entity.items.ItemsSelectionDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.tests.campuslostandfoundsystem.dao.ItemsDAO;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemsServiceImpl extends ServiceImpl<ItemsDAO, Items> implements ItemsService{
    private final ItemsDAO itemsDAO;

    @Operation(summary = "获取失物pages")
    @Override
    public Page<Items> getAllItemsPages(ItemsSelectionDTO dto) {
        //      查询条件
        QueryWrapper<Items> qw = new QueryWrapper<>();
        qw.eq("is_deleted", 0)
                .like(StringUtils.isNotBlank(dto.getName()), "title", dto.getName())
                .like(dto.getStatus()!= null, "status", dto.getStatus());
//      处理分页
        Long pageNumber = dto.getPageNumber() == null||dto.getPageNumber()<=0 ? 1L : dto.getPageNumber();
        Long pageSize = dto.getPageSize() == null||dto.getPageSize()<=0 ? 10L : dto.getPageSize();
        Page<Items> page = Page.of(pageNumber, pageSize);
        if(StringUtils.isNotBlank(dto.getSortBy())){
            String columnName= com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(dto.getSortBy());
            boolean isAsc = "asc".equalsIgnoreCase(columnName);
            qw.orderBy(true, isAsc, columnName);
        }else{
            qw.orderByDesc("create_time");
        }
        return itemsDAO.selectPage(page, qw);
    }
    @Operation(summary = "根据id删除失物信息")
    @Transactional
    @Override
    public void deleteItem(Long id) {
        itemsDAO.deleteById(id);
    }

    @Operation(summary = "查询失物信息")
    @Transactional
    @Override
    public void updateItem(Items items) {
   itemsDAO.updateById(items);
    }

    @Operation(summary = "新增失物信息")
    @Transactional
    @Override
    public void insertItem(Items items) {
   itemsDAO.insert(items);
    }

    @Override
    public ItemTypeCountDTO getItemTypeCount() {
        return itemsDAO.getItemTypeCount();
    }
}
