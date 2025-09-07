package com.tests.campuslostandfoundsystem.service.roles;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.RolesDAO;
import com.tests.campuslostandfoundsystem.entity.roles.Roles;

@Service
public class RolesServiceImpl extends ServiceImpl<RolesDAO, Roles> implements RolesService{

}
