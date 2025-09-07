package com.tests.campuslostandfoundsystem.service.itemsAdmins;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.entity.itemAdmins.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNotices;

public interface ItemAdminsService extends IService<ItemAdmins>{
    /**
     * 获取当前登录物品管理员的个人信息
     * @return 物品管理员信息DTO
     */
    ItemAdminsInfoDTO getItemAdminInfo();

    /**
     * 修改当前登录物品管理员的个人信息
     * @param changeItemAdminsDTO 包含待修改信息的DTO
     */
    void changeItemAdminInfo(ChangeItemAdminsDTO changeItemAdminsDTO);


}
