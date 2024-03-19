package acceptance.task.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.builder.WorkbasketBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "transfer task" scenarios. */
@TaskanaIntegrationTest
class TransferTaskAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  Task task0;
  ClassificationSummary defaultClassificationSummary;
  ObjectReference defaultObjectReference;
  WorkbasketSummary defaultWorkbasketSummary1;
  WorkbasketSummary defaultWorkbasketSummary2;
  WorkbasketSummary defaultWorkbasketSummary3;
  WorkbasketSummary defaultWorkbasketSummary4;
  WorkbasketSummary defaultWorkbasketSummary5;
  WorkbasketSummary defaultWorkbasketSummary6;
  WorkbasketSummary defaultWorkbasketSummary7;

  public static final String GROUP_1_DN =
      "cn=Organisationseinheit KSC 1,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA";

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {

    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultObjectReference = defaultTestObjectReference().build();

    defaultWorkbasketSummary1 =
        WorkbasketBuilder.newWorkbasket()
            .key("GPK_KSC")
            .name("Gruppenpostkorb KSC")
            .domain("DOMAIN_A")
            .type(WorkbasketType.GROUP)
            .owner("teamlead-1")
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary2 =
        WorkbasketBuilder.newWorkbasket()
            .key("TEAMLEAD-2")
            .name("PPK Teamlead KSC 2")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary3 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-1-1")
            .name("PPK User 1 KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary4 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-1-2")
            .name("PPK User 2 KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .owner("user-1-2")
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary5 =
        WorkbasketBuilder.newWorkbasket()
            .key("TPK_VIP")
            .name("Themenpostkorb VIP")
            .domain("DOMAIN_A")
            .type(WorkbasketType.TOPIC)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary6 =
        WorkbasketBuilder.newWorkbasket()
            .key("GPK_B_KSC_1")
            .name("Gruppenpostkorb KSC B1")
            .domain("DOMAIN_B")
            .type(WorkbasketType.GROUP)
            .description("Gruppenpostkorb KSC 1")
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary7 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-B-2")
            .name("PPK User 2 KSC 1 Domain B")
            .domain("DOMAIN_B")
            .type(WorkbasketType.PERSONAL)
            .owner("user-1-2")
            .buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary1.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary2.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary3.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary3.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary4.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary5.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary6.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary6.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    task0 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketIdIsProvided() throws Exception {
    taskService.claim(task0.getId());
    taskService.setTaskRead(task0.getId(), true);

    taskService.transfer(task0.getId(), defaultWorkbasketSummary3.getId());

    Task transferredTask = taskService.getTask(task0.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SetStateCorrectly_When_TransferringSingleTask() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task3 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task4 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    List<Pair<String, TaskState>> testCases =
        List.of(
            Pair.of(task1.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task2.getId(), TaskState.READY),
            Pair.of(task3.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task4.getId(), TaskState.READY));

    ThrowingConsumer<Pair<String, TaskState>> test =
        p -> {
          String taskId = p.getLeft();
          TaskState expectedState = p.getRight();
          taskService.transfer(taskId, defaultWorkbasketSummary3.getId());
          Task result = taskService.getTask(taskId);
          assertThat(result.getState()).isEqualTo(expectedState);
          taskService.transfer(taskId, defaultWorkbasketSummary4.getId());
        };

    return DynamicTest.stream(
        testCases.iterator(), p -> "Expected state: " + p.getRight().name(), test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetStateCorrectly_When_BulkTranferringTasks() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task3 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task task4 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    String readyForReview = task1.getId();
    String ready = task2.getId();
    String inReview = task3.getId();
    String claimed = task4.getId();
    List<String> taskIds = List.of(readyForReview, ready, inReview, claimed);

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary3.getId(), taskIds);

    assertThat(results.containsErrors()).isFalse();
    assertThat(taskService.getTask(readyForReview).getState())
        .isEqualTo(taskService.getTask(inReview).getState())
        .isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(taskService.getTask(ready).getState())
        .isEqualTo(taskService.getTask(claimed).getState())
        .isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TransferTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task task = taskService.getTask(task0.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), defaultWorkbasketSummary3.getId());

    Task transferredTask = taskService.getTask(task0.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    Task task = taskService.getTask(task0.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "USER-1-1", "DOMAIN_A");

    Task transferredTask = taskService.getTask(task0.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ChangeDomain_When_TransferringTaskToWorkbasketWithDifferentDomain() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary3)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task = taskService.getTask(task1.getId());
    String domain1 = task.getDomain();

    Task transferredTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.getDomain()).isNotEqualTo(domain1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorization() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary3)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task = taskService.getTask(task1.getId());

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), defaultWorkbasketSummary2.getId());
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorizationAndIsMemeberOfTaskRouterRole()
      throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary3)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task = taskService.getTask(task1.getId());

    assertThatThrownBy(() -> taskService.transfer(task.getId(), defaultWorkbasketSummary2.getId()))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_DestinationWorkbasketDoesNotExist() throws Exception {
    Task task = taskService.getTask(task0.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    ThrowingCallable call = () -> taskService.transfer(task.getId(), "INVALID");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferDoesNotExist() throws Exception {
    ThrowingCallable call =
        () -> taskService.transfer("Invalid", defaultWorkbasketSummary2.getId());
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TransferWithNoTransferAuthorization() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary2)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    ThrowingCallable call =
        () -> taskService.transfer(task1.getId(), defaultWorkbasketSummary1.getId());
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'teamlead-1' has no '[TRANSFER]' permission(s) "
                + "for Workbasket '"
                + defaultWorkbasketSummary2.getId()
                + "'.");
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferWithNoAppendAuthorization() throws Exception {
    WorkbasketSummary defaultWorkbasketSummary8 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-2-1")
            .name("PPK User 1 KSC 2")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .buildAndStoreAsSummary(workbasketService, "admin");
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary8.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService, "admin");
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary3)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task = taskService.getTask(task1.getId());

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), defaultWorkbasketSummary8.getId());
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'user-1-1' has no '[APPEND]' permission(s) "
                + "for Workbasket '"
                + defaultWorkbasketSummary8.getId()
                + "'.");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferIsAlreadyCompleted() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.COMPLETED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task = taskService.getTask(task1.getId());

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), defaultWorkbasketSummary2.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidTaskStateException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketIdIsProvided() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList = List.of(task1.getId(), task2.getId());

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary3.getId(), taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    Task transferredTask = taskService.getTask(task1.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask(task2.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferOnlyValidTasks_When_SomeTasksToTransferCauseExceptions()
      throws Exception {
    WorkbasketSummary defaultWorkbasketSummary8 =
        WorkbasketBuilder.newWorkbasket()
            .key("TEAMLEAD-1")
            .name("PPK Teamlead KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .buildAndStoreAsSummary(workbasketService, "admin");
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary8.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService, "admin");
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task3 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary5)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task4 =
        TaskBuilder.newTask()
            .state(TaskState.COMPLETED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary8)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    // we can't use List.of because of the null value we insert
    List<String> taskIdList =
        Arrays.asList(
            task1.getId(), // working
            task2.getId(), // NotAuthorized READ
            task2.getId(), // NotAuthorized READ
            task3.getId(), // NotAuthorized TRANSFER
            "", // InvalidArgument
            null, // InvalidArgument
            "TKI:000000000000000000000000000000000099", // not existing
            task4.getId()); // already completed

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary3.getId(), taskIdList);
    // check for exceptions in bulk
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().values()).hasSize(6);
    assertThat(results.getErrorForId(task2.getId()).getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId(task3.getId()).getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000099").getClass())
        .isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId(task4.getId()).getClass())
        .isEqualTo(InvalidTaskStateException.class);
    assertThat(results.getErrorForId("").getClass()).isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId(null).getClass()).isEqualTo(TaskNotFoundException.class);

    // verify valid requests
    Task transferredTask = taskService.getTask(task1.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();

    transferredTask = taskService.getTask(task3.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TPK_VIP");

    transferredTask = taskService.getTask(task4.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-1");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_BulkTransferTasksWithoutAppendPermissionOnTarget()
      throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    List<String> taskIdList =
        List.of(
            task1.getId(), // working
            task2.getId()); // NotAuthorized READ

    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary5.getId(), taskIdList);
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .hasMessageContaining("APPEND");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTasks_When_TransferringTasksWithListNotSupportingRemove() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    List<String> taskIds = List.of(task1.getId());

    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary3.getId(), taskIds);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferredTaskListIsNull() {
    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary3.getId(), null);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not be null or empty.");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringEmptyTaskIdList() {
    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary3.getId(), Collections.emptyList());
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList = List.of(task1.getId(), task2.getId());

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("GPK_B_KSC_1", "DOMAIN_B", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("GPK_B_KSC_1", "DOMAIN_B");
    Task transferredTask = taskService.getTask(task1.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask(task2.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlag_When_SetTransferFlagNotRequested() throws Exception {
    taskService.transfer(task0.getId(), defaultWorkbasketSummary3.getId(), false);

    Task transferredTask = taskService.getTask(task0.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlagWithinBulkTransfer_When_SetTransferFlagNotRequested()
      throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");

    taskService.transferTasks(
        defaultWorkbasketSummary3.getId(),
        List.of(task0.getId(), task1.getId(), task2.getId()),
        false);

    List<TaskSummary> transferredTasks =
        taskService.createTaskQuery().idIn(task1.getId(), task2.getId(), task0.getId()).list();

    assertThat(transferredTasks).extracting(TaskSummary::isTransferred).containsOnly(false);
  }
}
