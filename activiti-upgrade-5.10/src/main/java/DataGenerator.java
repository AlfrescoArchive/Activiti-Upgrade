import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Joram Barrez
 */
public class DataGenerator {
  
  public static void main(String[] args) {
    ProcessEngine processEngine = UpgradeUtil.getProcessEngine();
    createCommonData(processEngine);
    create511SpecificData(processEngine);
  }
  
  private static void createCommonData(ProcessEngine processEngine) {
    // UpgradeTaskOneTest in 5.8
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();

    processEngine.getRepositoryService()
      .createDeployment()
      .name("simpleTaskProcess")
      .addClasspathResource("org/activiti/upgrade/test/UserTaskBeforeTest.testSimplestTask.bpmn20.xml")
      .deploy();

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simpleTaskProcess");
    String taskId = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult().getId();
    taskService.complete(taskId);
    
    // UpgradeTaskTwoTest in 5.8
    processEngine.getRepositoryService().createDeployment().name("simpleTaskProcess")
            .addClasspathResource("org/activiti/upgrade/test/UserTaskBeforeTest.testTaskWithExecutionVariables.bpmn20.xml")
            .deploy();

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("instrument", "trumpet");
    variables.put("player", "gonzo");
    runtimeService.startProcessInstanceByKey("taskWithExecutionVariablesProcess", variables);
  }
  
  private static void create511SpecificData(ProcessEngine processEngine) {
    // VerifyProcessDefinitionDescriptionTest in 5.11
    processEngine.getRepositoryService()
      .createDeployment()
      .name("verifyProcessDefinitionDescription")
      .addClasspathResource("org/activiti/upgrade/test/VerifyProcessDefinitionDescriptionTest.bpmn20.xml")
      .deploy();
    
    // SuspendAndActivateFunctionalityTest in 5.12
    // Deploy test process, and start a few process instances
    Deployment deployment = processEngine.getRepositoryService().createDeployment()
      .name("SuspendAndActivateFunctionalityTest")
      .addClasspathResource("org/activiti/upgrade/test/SuspendAndActivateUpgradeTest.bpmn20.xml")
      .deploy();
    
    ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
            .deploymentId(deployment.getId())
            .singleResult();
    
    for (int i=0; i<5; i++) {
      processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
    }
  }

}
