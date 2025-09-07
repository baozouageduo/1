package com.tests.campuslostandfoundsystem.service.lostPropertyNotices;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNoticesSelectionDTO;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNotices;

public interface LostPropertyNoticesService extends IService<LostPropertyNotices> {
    Page<LostPropertyNotices> getAllLostPropertyNoticesPages(LostPropertyNoticesSelectionDTO dto);
    void insertLostPropertyNotices(LostPropertyNotices lostPropertyNotices);
    void updateLostPropertyNotices(LostPropertyNotices lostPropertyNotices);
    void deleteLostPropertyNotices(Long id);

}
