package com.tests.campuslostandfoundsystem.service.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tests.campuslostandfoundsystem.dao.PermissionsDAO;
import com.tests.campuslostandfoundsystem.dao.RolesDAO;
import com.tests.campuslostandfoundsystem.dao.UserDAO;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.enums.exception.UserResultCodes;
import com.tests.campuslostandfoundsystem.entity.permission.Permissions;
import com.tests.campuslostandfoundsystem.entity.roles.Roles;
import com.tests.campuslostandfoundsystem.entity.user.Profiles;
import com.tests.campuslostandfoundsystem.entity.user.Users;
import com.tests.campuslostandfoundsystem.exception.UserException;
import com.tests.campuslostandfoundsystem.service.roles.RolesService;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomsDetailsService implements UserDetailsService {
    private final UserDAO userDAO;
    private final RolesDAO  rolesDAO;
    private final PermissionsDAO  permissionsDAO;
    private final RolesService rolesService;
    @Operation(summary = "通过用户id获取信息")
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try{
//          查user信息
            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",userId)
                        .eq("is_delete",0);
            Users user = userDAO.selectOne(queryWrapper);
            if(user == null){
                throw new UserException(UserResultCodes.USER_NOT_FOUND,"用户不存在");
            }
//          查新role信息
            QueryWrapper<Roles> qwRoles = new QueryWrapper<>();
            qwRoles.eq("is_delete",0).select("role_name");
            List<Objects> ojRoleList = rolesDAO.selectObjs(qwRoles);
            List<String> roleList = ojRoleList.stream()
                    .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
//          查询permission信息
            QueryWrapper<Permissions>  qwPermissions = new QueryWrapper<>();
            qwPermissions.eq("is_delete",0).select("permission_name");
            List<Objects> ojPermissionList =permissionsDAO.selectObjs(qwPermissions);
            List<String> permissionList = ojPermissionList.stream()
                    .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
//          查询profiles信息
            List<Profiles> profilesList=userDAO.selectUsersProfilesByUserId(userId);
            return CustomsUserDetail.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roleList)
                    .permissions(permissionList)
                    .profiles(profilesList)
                    .build();
        }catch (Exception e){
           throw new UserException(UserResultCodes.USER_NOT_FOUND,"用户加载失败:"+e.getMessage(),e);
        }
    }
}
