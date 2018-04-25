/**
 * 
 */
package com.tantan.workflow.config;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import com.tantan.workflow.listener.ProcessCompleteListener;

/**
 * 
 * @author pursue2020
 *
 */
@Slf4j
@Configuration
public class WorkflowConfig extends AbstractProcessEngineAutoConfiguration implements WorkFlowConfigurable {
	
	/**
	 * 注入全局开始监听事件
	 */
	@Autowired
	ProcessCompleteListener processCompleteListener;
    
    @Resource
    PlatformTransactionManager activitiTransactionManager;//注入配置好的事物管理器

	
    @Bean(name="workflowDataSource")
    @Primary
    @ConfigurationProperties(prefix = "workflow.jdbc")
    public DataSource workflowDataSource(){
    	 DataSource dataSource= DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    	 log.info("工作流引擎数据源创建完毕");
    	 return dataSource;
    }



    //注入数据源和事务管理器
    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
        SpringAsyncExecutor springAsyncExecutor) throws IOException {
    	log.info(StringUtils.center("添加全局注册事件", 100,"=")); 
    	SpringProcessEngineConfiguration configuration=this.baseSpringProcessEngineConfiguration(workflowDataSource(), activitiTransactionManager, springAsyncExecutor);
    	configuration.setEventListeners(Collections.singletonList(processCompleteListener));

    	log.info("设置全局监听事件成功");
        return configuration;
    }


    


   
    

}
