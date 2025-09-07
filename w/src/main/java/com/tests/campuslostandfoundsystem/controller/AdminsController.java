package com.tests.campuslostandfoundsystem.controller;
import com.tests.campuslostandfoundsystem.entity.R;
import com.tests.campuslostandfoundsystem.entity.admins.ItemsAdminsInfoDTO;
import com.tests.campuslostandfoundsystem.entity.admins.ChangeAdminsDTO;
import com.tests.campuslostandfoundsystem.entity.admins.ItemTypeCountDTO;
import com.tests.campuslostandfoundsystem.service.admin.AdminsService;
import com.tests.campuslostandfoundsystem.service.items.ItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
* 管理员信息表(admins)表控制层
*
* @author xxxxx
*/
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admins")
public class AdminsController {
    private final AdminsService adminsService;
    private final ItemsService itemsService;

    @GetMapping("/me")
    public R<ItemsAdminsInfoDTO> getAdminInfo(){
        return R.success(adminsService.getAdminInfo());
    }

    @PutMapping("/me")
    public R<Void> changeAdminInfo(ChangeAdminsDTO changeAdminsDTO){
        adminsService.changeAdminInfo(changeAdminsDTO);
        return R.success(null);
    }

    @GetMapping("/itemTypeCount")
    public R<ItemTypeCountDTO> getItemTypeCount(){
        return R.success(itemsService.getItemTypeCount());
    }
}
