package com.tests.campuslostandfoundsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tests.campuslostandfoundsystem.entity.R;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNotices;
import com.tests.campuslostandfoundsystem.entity.notice.LostPropertyNoticesSelectionDTO;
import com.tests.campuslostandfoundsystem.service.lostPropertyNotices.LostPropertyNoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lost")
public class LostPropertyNoticesController {
    private final LostPropertyNoticesService lostPropertyNoticesService;
    @GetMapping("/")
    public R<Page<LostPropertyNotices>> getAllLostPropertyNoticesPages(LostPropertyNoticesSelectionDTO dto){
        return R.success(lostPropertyNoticesService.getAllLostPropertyNoticesPages(dto));
    }
    @PostMapping("/")
    public R<Void> insertLostPropertyNotices(LostPropertyNotices lostPropertyNotices){
        lostPropertyNoticesService.insertLostPropertyNotices(lostPropertyNotices);
        return R.success(null);
    }
    @PutMapping("/")
    public R<Void> updateLostPropertyNotices(LostPropertyNotices lostPropertyNotices){
        lostPropertyNoticesService.updateLostPropertyNotices(lostPropertyNotices);
        return R.success(null);
    }
    @DeleteMapping("/{id}")
    public R<Void> deleteLostPropertyNotices( @PathVariable("id") Long id){
        lostPropertyNoticesService.deleteLostPropertyNotices(id);
        return R.success(null);
    }
}
