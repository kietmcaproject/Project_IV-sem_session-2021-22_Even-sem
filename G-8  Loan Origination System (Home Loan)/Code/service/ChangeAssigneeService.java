package com.kuliza.workbench.service;

import java.util.List;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ChangeAssigneeService")
public class ChangeAssigneeService {
  private static final Logger logger = LoggerFactory.getLogger(ChangeAssigneeService.class);

  @Autowired private CmmnTaskService cmmnTaskService;
  @Autowired private CmmnRuntimeService cmmnRuntimeService;

  public DelegatePlanItemInstance changeAssignee(
      DelegatePlanItemInstance planItemInstance,
      String oldAssignee,
      String newAssignee,
      String roleKey,
      String roleAssignee) {

    logger.info("------------------- IN : changeAssignee ------------------- ");
    logger.info(
        "oldAssignee : {},  newAssignee : {}, roleKey : {} , roleAssignee : {}",
        oldAssignee,
        newAssignee,
        roleKey,
        roleAssignee);
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    logger.info("-------- caseInstanceId : {}", caseInstanceId);
    List<Task> tasks =
        cmmnTaskService
            .createTaskQuery()
            .caseInstanceId(caseInstanceId)
            .taskAssignee(oldAssignee)
            .active()
            .list();

    logger.info("------------------ tasks before assigneeChanged : {}", tasks);

    List<Task> tasks1 =
        cmmnTaskService
            .createTaskQuery()
            .caseInstanceId(planItemInstance.getCaseInstanceId())
            .list();
    logger.info("---------- tasks1 : {}", tasks1);

    if (tasks1 != null) {
      tasks1.forEach(
          task -> {
            if (task.getAssignee().equalsIgnoreCase(oldAssignee)) task.setAssignee(newAssignee);
          });
    }

    if (tasks != null) {
      for (Task task : tasks) {
        logger.info("Task1 : {}, Assignee : {} ", task, task.getAssignee());
      }
    }

    List<Task> tasks2 =
        cmmnTaskService
            .createTaskQuery()
            .caseInstanceId(planItemInstance.getCaseInstanceId())
            .processVariableValueEquals(roleKey + "Assignee", oldAssignee)
            .taskAssignee(oldAssignee)
            .list();
    logger.info("---------- tasks2 : {}", tasks2);

    if (tasks != null) {
      for (Task task : tasks) {
        task.setAssignee(newAssignee);
      }
    }
    logger.info("---------- tasks to change assignee : {}", tasks);

    if (tasks != null) {
      tasks.forEach(
          task -> {
            task.setAssignee(newAssignee);
          });
    }
    cmmnRuntimeService.setVariable(caseInstanceId, roleAssignee, newAssignee);

    if (tasks != null) {
      for (Task task : tasks) {
        logger.info("----- Task : {}, Assignee : {} ", task, task.getAssignee());
      }
    }

    return planItemInstance;
  }
}
