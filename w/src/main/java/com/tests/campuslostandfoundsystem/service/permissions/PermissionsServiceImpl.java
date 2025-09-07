package com.tests.campuslostandfoundsystem.service.permissions;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.PermissionsDAO;
import com.tests.campuslostandfoundsystem.entity.permission.Permissions;

@Service
public class PermissionsServiceImpl extends ServiceImpl<PermissionsDAO, Permissions> implements PermissionsService{

}
