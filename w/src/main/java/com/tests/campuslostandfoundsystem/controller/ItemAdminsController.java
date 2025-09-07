package com.tests.campuslostandfoundsystem.controller;
import com.tests.campuslostandfoundsystem.entity.R;
import com.tests.campuslostandfoundsystem.entity.admins.ItemsAdminsInfoDTO;
import com.tests.campuslostandfoundsystem.entity.admins.ChangeAdminsDTO;
import com.tests.campuslostandfoundsystem.entity.itemAdmins.ChangeItemAdminsDTO;
import com.tests.campuslostandfoundsystem.entity.itemAdmins.ItemAdminsInfoDTO;
import com.tests.campuslostandfoundsystem.service.itemsAdmins.ItemAdminsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
* 物品管理员信息表(item_admins)表控制层
*
* @author xxxxx
*/
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ITEM_ADMIN')")
@RequestMapping("/itemAdmins")
public class ItemAdminsController {
    private final ItemAdminsService itemAdminsService;

    @GetMapping("/me")
    public R<ItemAdminsInfoDTO> getAdminInfo(){
        return R.success(itemAdminsService.getItemAdminInfo());
    }

    @PutMapping("/me")
    public R<Void> changeAdminInfo(ChangeItemAdminsDTO dto){
        itemAdminsService.changeItemAdminInfo(dto);
        return R.success(null);
    }

}
