package com.tests.campuslostandfoundsystem.service.itemsAdmins;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.dao.ItemsDAO;
import com.tests.campuslostandfoundsystem.dao.LostPropertyNoticesDAO;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.itemAdmins.*;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNotices;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.ItemAdminsDAO;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemAdminsServiceImpl extends ServiceImpl<ItemAdminsDAO, ItemAdmins> implements ItemAdminsService{
    private final LostPropertyNoticesDAO lostPropertyNoticesDAO;
    private final ItemsDAO itemsDAO;
    private final UserService userService;
    private final String profileType = "ITEM_ADMIN";

    @Operation(summary = "获取物品管理员信息")
    @Override
    public ItemAdminsInfoDTO getItemAdminInfo() {
        // 获取当前登录用户信息
        CustomsUserDetail userInfo = userService.getUserInfo();
        Long profileId = userInfo.getProfileId(profileType);

        // 查询物品管理员信息
        ItemAdmins itemAdmin = this.getById(profileId);

        // 封装并返回DTO
        return new ItemAdminsInfoDTO(itemAdmin.getId(), itemAdmin.getName(), itemAdmin.getPlaceId());
    }

    @Operation(summary = "修改物品管理员信息")
    @Override
    public void changeItemAdminInfo(ChangeItemAdminsDTO changeItemAdminsDTO) {
        CustomsUserDetail userInfo = userService.getUserInfo();
        Long profileId = userInfo.getProfileId(profileType);

        UpdateWrapper<ItemAdmins> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", profileId); // 确保只更新当前登录的物品管理员

        // 动态设置更新字段
        updateWrapper.set(StringUtils.isNotBlank(changeItemAdminsDTO.getName()), "name", changeItemAdminsDTO.getName());
        updateWrapper.set(StringUtils.isNotBlank(changeItemAdminsDTO.getPassword()), "password", changeItemAdminsDTO.getPassword());
        updateWrapper.set(changeItemAdminsDTO.getPlaceId() != null, "place_id", changeItemAdminsDTO.getPlaceId());

        this.update(updateWrapper);
    }




}
