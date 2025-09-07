package com.tests.campuslostandfoundsystem.service.places;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tests.campuslostandfoundsystem.dao.PlacesDAO;
import com.tests.campuslostandfoundsystem.entity.places.Places;

@Service
public class PlacesServiceImpl extends ServiceImpl<PlacesDAO, Places> implements PlacesService{

}
