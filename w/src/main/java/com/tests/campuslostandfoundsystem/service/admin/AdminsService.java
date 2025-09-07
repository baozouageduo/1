package com.tests.campuslostandfoundsystem.service.admin;

import com.tests.campuslostandfoundsystem.entity.admins.*;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdminsService extends IService<Admins>{
    /**
     * 获取当前登录管理员的个人信息
     * @return 管理员信息DTO
     */
    ItemsAdminsInfoDTO getAdminInfo();

    /**
     * 修改当前登录管理员的个人信息
     * @param changeAdminsDTO 包含待修改信息的DTO
     */
    void changeAdminInfo(ChangeAdminsDTO changeAdminsDTO);



}
