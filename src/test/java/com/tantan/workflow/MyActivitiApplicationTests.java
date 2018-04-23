package com.tantan.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tantan.workflow.constants.Constants;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyActivitiApplicationTests {
	
    static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd hh:mm:ss").create();

	ProcessEngine processEngine;

	@Before
	public void get() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
		log.info(StringUtils.center("获取processEngine",100,"="));
	}

	/**
	 * 部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine
				.getRepositoryService()
				// 与流程定义和部署对象相关的RepositoryService
				.createDeployment()
				// 创建一个部署对象
				.name("测试任务")
				// 添加部署名称
				.addClasspathResource(
						"com/tantan/workflow/personal/task/MyTask.bpmn")
				// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource(
						"com/tantan/workflow/personal/task/MyTask.png")
				.deploy();
		log.info("部署ID：" + deployment.getId());
		log.info("部署名称：" + deployment.getName());

	}

	/**
	 * 启动流程实例
	 */
	@Test
	public void startProcessInstance() {
		// 流程定义的KEY
		String processDefinitionKey = "workflow2";
		Map<String, Object> variables = new HashMap<String, Object>();  
        variables.put("appUser", "张三");  
        variables.put("fristUser", "李四");

		ProcessInstance pi = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey,variables);
		// 使用流程定义的key启动流程实例，key对应MyTask.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动

		log.info("流程实例ID:" + pi.getId());// 流程实例ID:7501
		log.info("流程定义ID:" + pi.getProcessDefinitionId());// 流程定义ID:myProcess:3:5004
	}

	@Test
	public void findMyPersonalTask() {
		String assignee = "张三";
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.createTaskQuery()// 创建任务查询对象
				/** 查询条件（where部分） */
				//.taskAssignee(assignee)// 指定个人任务查询，指定办理人
				.taskCandidateOrAssigned(assignee)
				// .taskCandidateUser(candidateUser)//组任务的办理人查询
				// .processDefinitionId(processDefinitionId)//使用流程定义ID查询
				// .processInstanceId(processInstanceId)//使用流程实例ID查询
				// .executionId(executionId)//使用执行对象ID查询
				/** 排序 */
				.orderByTaskCreateTime().asc()// 使用创建时间的升序排列
				/** 返回结果集 */
				// .singleResult()//返回惟一结果集
				// .count()//返回结果集的数量
				// .listPage(firstResult, maxResults);//分页查询
				.list();// 返回列表
		
		if(list!=null && list.size()>0){  
            for(Task task:list){  
                log.info("任务ID:"+task.getId());  
                log.info("任务名称:"+task.getName());  
                log.info("任务的创建时间:"+task.getCreateTime());  
                log.info("任务的办理人:"+task.getAssignee());  
                log.info("流程实例ID："+task.getProcessInstanceId());  
                log.info("执行对象ID:"+task.getExecutionId());  
                log.info("流程定义ID:"+task.getProcessDefinitionId());  
                log.info("########################################################");  
            }  
        }
		
	}
	
	 /**完成我的任务*/  
    @Test  
    public void completeMyPersonalTask(){  
        //任务ID  
        String taskId = "92506";  
        
        Map<String, Object> variables = new HashMap<String, Object>();  
       // variables.put("outcome", Constants.ProcessAuditStatus.IS_NOT_AGREE);  //模拟驳回处理
        variables.put("message", Constants.ProcessAuditStatus.IS_AGREE);  //模拟驳回处理
        processEngine.getTaskService()//与正在执行的任务管理相关的Service  
                    .complete(taskId,variables);  
        log.info("完成任务：任务ID："+taskId);  
    }  
	
    //可以分配个人任务从一个人到另一个人（认领任务）  
    @Test  
    public void setAssigneeTask(){  
        //任务ID  
        String taskId = "7504";  
        //指定的办理人  
        String userId = "张翠山";  
        processEngine.getTaskService()//  
                    .setAssignee(taskId, userId);  
    } 
	
	

}
