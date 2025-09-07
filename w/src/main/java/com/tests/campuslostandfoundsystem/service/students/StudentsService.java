package com.tests.campuslostandfoundsystem.service.students;

import com.tests.campuslostandfoundsystem.entity.student.ChangeStudentDTO;
import com.tests.campuslostandfoundsystem.entity.student.Students;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tests.campuslostandfoundsystem.entity.student.StudentsInfoDTO;

public interface StudentsService extends IService<Students>{
    public StudentsInfoDTO getStudentInfo();
    public void changeStudentInfo(ChangeStudentDTO changeStudentDTO);
}
