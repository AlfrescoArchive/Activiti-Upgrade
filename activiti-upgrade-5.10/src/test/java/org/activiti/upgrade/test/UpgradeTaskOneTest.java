package org.activiti.upgrade.test;



import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.upgrade.test.helper.UpgradeTestCase;

public class UpgradeTaskOneTest extends UpgradeTestCase {
  
  public void testSimplestTask() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();
    ManagementService managementService = processEngine.getManagementService();
    HistoryService historyService = processEngine.getHistoryService();

    Task task = taskService
      .createTaskQuery()
      .taskName("simpleTask2")
      .singleResult();
    
    String processInstanceId = task.getProcessInstanceId();
    
    long expectedHistoryTaskInstances = -1;
    String schemaHistory = managementService.getProperties().get("schema.history");
    if (schemaHistory.startsWith("create(5.0)")) {
      expectedHistoryTaskInstances = 0;
    } else {
      expectedHistoryTaskInstances = 2;
    }
    
    assertEquals(expectedHistoryTaskInstances, 
      historyService.createHistoricTaskInstanceQuery()
        .processInstanceId(processInstanceId)
        .orderByTaskName().asc()
        .count());
      
    taskService.complete(task.getId());
    
    assertEquals(1, runtimeService
            .createExecutionQuery()
            .processInstanceId(processInstanceId)
            .list()
            .size());

    assertEquals(expectedHistoryTaskInstances+1, 
            historyService.createHistoricTaskInstanceQuery()
              .processInstanceId(processInstanceId)
              .orderByTaskName().asc()
              .count());
            
    task = taskService
      .createTaskQuery()
      .taskName("simpleTask3")
      .singleResult();

    taskService.complete(task.getId());

    assertEquals(0, runtimeService
            .createExecutionQuery()
            .processInstanceId(processInstanceId)
            .list()
            .size());

    assertEquals(expectedHistoryTaskInstances+1, 
            historyService.createHistoricTaskInstanceQuery()
              .processInstanceId(processInstanceId)
              .orderByTaskName().asc()
              .count());
  }
}
