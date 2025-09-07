package com.tests.campuslostandfoundsystem.service.admin;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tests.campuslostandfoundsystem.dao.ItemsDAO;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.admins.ItemsAdminsInfoDTO;
import com.tests.campuslostandfoundsystem.entity.admins.ChangeAdminsDTO;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.AdminsDAO;
import com.tests.campuslostandfoundsystem.entity.admins.Admins;

@Service
@RequiredArgsConstructor
public class AdminsServiceImpl extends ServiceImpl<AdminsDAO, Admins> implements AdminsService{
    private final ItemsDAO  itemsDAO;
    private final UserService userService;
    private final String profileType = "ADMIN";

    @Operation(summary = "获取管理员信息")
    @Override
    public ItemsAdminsInfoDTO getAdminInfo() {
        // 获取当前登录用户信息
        CustomsUserDetail userInfo = userService.getUserInfo();
        Long profileId = userInfo.getProfileId(profileType);

        // 查询管理员信息
        Admins admin = this.getById(profileId);

        // 封装并返回DTO
        return new ItemsAdminsInfoDTO(admin.getId(), admin.getName());
    }

    @Operation(summary = "修改管理员信息")
    @Override
    public void changeAdminInfo(ChangeAdminsDTO changeAdminsDTO) {
        CustomsUserDetail userInfo = userService.getUserInfo();
        Long profileId = userInfo.getProfileId(profileType);

        UpdateWrapper<Admins> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", profileId); // 确保只更新当前登录的管理员

        // 动态设置更新字段
        updateWrapper.set(StringUtils.isNotBlank(changeAdminsDTO.getName()), "name", changeAdminsDTO.getName());
        updateWrapper.set(StringUtils.isNotBlank(changeAdminsDTO.getPassword()), "password", changeAdminsDTO.getPassword());

        this.update(updateWrapper);
    }

}
