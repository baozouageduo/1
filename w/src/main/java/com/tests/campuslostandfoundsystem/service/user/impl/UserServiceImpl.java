package com.tests.campuslostandfoundsystem.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.UserDAO;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.admins.UserSelectionDTO;
import com.tests.campuslostandfoundsystem.entity.enums.exception.AuthResultCodes;
import com.tests.campuslostandfoundsystem.entity.enums.exception.UserResultCodes;
import com.tests.campuslostandfoundsystem.entity.items.Items;
import com.tests.campuslostandfoundsystem.entity.user.Profiles;
import com.tests.campuslostandfoundsystem.entity.user.Users;
import com.tests.campuslostandfoundsystem.exception.UserException;
import com.tests.campuslostandfoundsystem.exception.UtilsException;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserDAO, Users> implements UserService {
    private final UserDAO userDAO;
    @Override
    public CustomsUserDetail getUserInfo() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null ) {
                throw new UserException(AuthResultCodes.AUTHENTICATING_EMPTY, "Authentication是空的");
            }
            if(!(authentication.getPrincipal() instanceof CustomsUserDetail user)){
                throw new UserException(AuthResultCodes.AUTHENTICATING_TYPE_ERROR, "Authentication的类型错误");
            }
            return user;
        }catch (Exception e){
            throw  new UserException(UserResultCodes.GET_USER_INFO_FAILED, "获取用户信息失败");
        }

    }

    @Override
    public List<Profiles> selectUsersProfilesByUserId(String userId) {
        return userDAO.selectUsersProfilesByUserId(userId);
    }

    @Transactional
    @Override
    public void updateUser(Users user) {
          userDAO.updateById(user);
    }

    @Override
    public Page<Users> getAllUsersPages(UserSelectionDTO dto) {
        //      查询条件
        QueryWrapper<Users> qw = new QueryWrapper<>();
        qw.eq("is_deleted", 0)
                .like(StringUtils.isNotBlank(dto.getUsername()), "username", dto.getUsername());
//      处理分页
        Long pageNumber = dto.getPageNumber() == null||dto.getPageNumber()<=0 ? 1L : dto.getPageNumber();
        Long pageSize = dto.getPageSize() == null||dto.getPageSize()<=0 ? 10L : dto.getPageSize();
        Page<Users> page = Page.of(pageNumber, pageSize);
        if(StringUtils.isNotBlank(dto.getSortBy())){
            String columnName= com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(dto.getSortBy());
            boolean isAsc = "asc".equalsIgnoreCase(columnName);
            qw.orderBy(true, isAsc, columnName);
        }else{
            qw.orderByDesc("create_time");
        }
        return userDAO.selectPage(page, qw);

    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userDAO.deleteById(userId);
    }

    @Transactional
    @Override
    public void insertUser(Users user) {
       userDAO.insert(user);
    }
}
