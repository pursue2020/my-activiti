/**
 * 
 */
package com.tantan.workflow.config;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 
 * @author pursue2020
 *
 */
@Slf4j
@Configuration
public class WorkflowConfig implements WorkFlowConfigurable {
	

	
    @Bean(name="workflowDataSource")
    @Primary
    @ConfigurationProperties(prefix = "workflow.jdbc")
    public DataSource workflowDataSource(){
    	 DataSource dataSource= DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    	 log.info("工作流引擎数据源创建完毕");
    	 return dataSource;
    }
   


}
