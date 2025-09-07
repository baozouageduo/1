package com.tests.campuslostandfoundsystem.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }

    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor(){
        return new OptimisticLockerInnerInterceptor();
    }

    @Bean
    @ConditionalOnBean(MultiDataPermissionHandler.class)
    public DataPermissionInterceptor dataPermissionInterceptor(@Qualifier("compositeDataPermissionHandler") MultiDataPermissionHandler multiDataPermissionHandler){
        return new DataPermissionInterceptor(multiDataPermissionHandler);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            ObjectProvider<List<InnerInterceptor>> interceptors
    ) {
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        interceptors.ifAvailable(list->list.forEach(mp::addInnerInterceptor));
        return mp;
    }
}
