package com.tests.campuslostandfoundsystem.service.students;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.student.ChangeStudentDTO;
import com.tests.campuslostandfoundsystem.entity.student.StudentsInfoDTO;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.entity.student.Students;
import com.tests.campuslostandfoundsystem.dao.StudentsDAO;

@Service
@RequiredArgsConstructor
public class StudentsServiceImpl extends ServiceImpl<StudentsDAO, Students> implements StudentsService{
    private final UserService userService;
    private String profileType = "STUDENT";

    @Operation(summary = "获取学生信息")
    @Override
    public StudentsInfoDTO getStudentInfo() {
//      获取当前登录用户信息
        CustomsUserDetail userInfo = userService.getUserInfo();
        Long profileId = userInfo.getProfileId(profileType);
//      查询学生信息
        Students student = this.getById(profileId);
        return new StudentsInfoDTO(student.getId(),
                student.getStudentNo(),
                student.getName(),
                student.getGender(),
                student.getAvatar(),
                student.getAge());
    }

    @Override
    public void changeStudentInfo(ChangeStudentDTO changeStudentDTO) {
        UpdateWrapper<Students> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(StringUtils.isNotBlank(changeStudentDTO.getStudentNo()), "student_no", changeStudentDTO.getStudentNo());
        updateWrapper.set(StringUtils.isNotBlank(changeStudentDTO.getName()), "name", changeStudentDTO.getName());
        updateWrapper.set(StringUtils.isNotBlank(changeStudentDTO.getPassword()), "password", changeStudentDTO.getPassword());
        updateWrapper.set(StringUtils.isNotBlank(changeStudentDTO.getGender()),"gender" , changeStudentDTO.getGender());
        updateWrapper.set(changeStudentDTO.getAge() != null, "age", changeStudentDTO.getAge());
        this.update(updateWrapper);
    }
}
