package com.tests.campuslostandfoundsystem.service.lostPropertyNotices.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.LostPropertyNoticesDAO;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNoticesSelectionDTO;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNotices;
import com.tests.campuslostandfoundsystem.service.lostPropertyNotices.LostPropertyNoticesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LostPropertyNoticesServiceImpl extends ServiceImpl<LostPropertyNoticesDAO, LostPropertyNotices> implements LostPropertyNoticesService {
    private final LostPropertyNoticesDAO lostPropertyNoticesDAO;

    @Transactional
    @Override
    public void deleteLostPropertyNotices(Long id) {
        lostPropertyNoticesDAO.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLostPropertyNotices(LostPropertyNotices lostPropertyNotices) {
        lostPropertyNoticesDAO.updateById(lostPropertyNotices);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertLostPropertyNotices(LostPropertyNotices lostPropertyNotices) {
        lostPropertyNoticesDAO.insert(lostPropertyNotices);
    }

    @Operation(summary = "获取待审核寻物物品列表")
    @Override
    public Page<LostPropertyNotices> getAllLostPropertyNoticesPages(LostPropertyNoticesSelectionDTO dto) {
//      查询条件
        QueryWrapper<LostPropertyNotices> qw = new QueryWrapper<>();
        qw.eq("is_deleted", 0)
                .like(StringUtils.isNotBlank(dto.getTitle()), "title", dto.getTitle())
                .like(StringUtils.isNotBlank(dto.getDescription()), "description", dto.getDescription())
                .like(dto.getAuditStatus()!= null, "audit_status", dto.getAuditStatus());
//      处理分页
        Long pageNumber = dto.getPageNumber() == null||dto.getPageNumber()<=0 ? 1L : dto.getPageNumber();
        Long pageSize = dto.getPageSize() == null||dto.getPageSize()<=0 ? 10L : dto.getPageSize();
        Page<LostPropertyNotices> page = Page.of(pageNumber, pageSize);
        if(StringUtils.isNotBlank(dto.getSortBy())){
            String columnName= com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(dto.getSortBy());
            boolean isAsc = "asc".equalsIgnoreCase(columnName);
            qw.orderBy(true, isAsc, columnName);
        }else{
            qw.orderByDesc("create_time");
        }
        return lostPropertyNoticesDAO.selectPage(page, qw);
    }
}
