package com.tests.campuslostandfoundsystem.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.admins.UserSelectionDTO;
import com.tests.campuslostandfoundsystem.entity.user.Profiles;
import com.tests.campuslostandfoundsystem.entity.user.Users;

import java.util.List;

public interface UserService extends IService<Users> {
      CustomsUserDetail getUserInfo();
      List<Profiles> selectUsersProfilesByUserId(String userId);
      void updateUser(Users user);
     Page<Users> getAllUsersPages(UserSelectionDTO dto);
     void insertUser(Users user);
     void deleteUser(Long userId);
}
