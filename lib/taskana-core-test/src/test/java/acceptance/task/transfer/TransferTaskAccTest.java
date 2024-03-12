package acceptance.task.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
import pro.taskana.task.api.CallbackState;
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
  Task task003;
  Task task025;
  Task task125;
  Task task225;
  Task task026;
  Task task00;
  Task task001;
  Task task207;
  Task task002;
  Task task106;
  Task task004;
  Task task005;
  Task task006;
  Task task041;
  Task task208;
  Task task023;
  Task task024;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary6;
  WorkbasketSummary defaultWorkbasketSummary7;
  WorkbasketSummary defaultWorkbasketSummary12;
  WorkbasketSummary defaultWorkbasketSummary5;
  WorkbasketSummary defaultWorkbasketSummary1;
  WorkbasketSummary defaultWorkbasketSummary8;
  WorkbasketSummary defaultWorkbasketSummary10;
  WorkbasketSummary defaultWorkbasketSummary4;
  WorkbasketSummary defaultWorkbasketSummary15;
  ObjectReference defaultObjectReference;
  public static final String GROUP_1_DN =
      "cn=Organisationseinheit KSC 1,cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA";

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    String stringDate = "2018-01-29 15:55:03";
    String pattern = "yyyy-M-d HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.GERMANY);
    LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
    ZoneId zoneId = ZoneId.of("Europe/Berlin");
    ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
    Instant created = zonedDateTime.toInstant();

    stringDate = "2018-01-30 15:55:00";
    dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.GERMANY);
    localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
    zonedDateTime = localDateTime.atZone(zoneId);
    Instant due = zonedDateTime.toInstant();

    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultObjectReference = defaultTestObjectReference().build();
    defaultWorkbasketSummary6 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-1-1")
            .name("PPK User 1 KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .description("PPK User 1 KSC 1")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary7 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-1-2")
            .name("PPK User 2 KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .description("PPK User 2 KSC 1")
            .owner("user-1-2")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary12 =
        WorkbasketBuilder.newWorkbasket()
            .key("GPK_B_KSC_1")
            .name("Gruppenpostkorb KSC B1")
            .domain("DOMAIN_B")
            .type(WorkbasketType.GROUP)
            .description("Gruppenpostkorb KSC 1")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary5 =
        WorkbasketBuilder.newWorkbasket()
            .key("TEAMLEAD-2")
            .name("PPK Teamlead KSC 2")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .description("PPK Teamlead KSC 2")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary1 =
        WorkbasketBuilder.newWorkbasket()
            .key("GPK_KSC")
            .name("Gruppenpostkorb KSC")
            .domain("DOMAIN_A")
            .type(WorkbasketType.GROUP)
            .description("Gruppenpostkorb KSC")
            .owner("teamlead-1")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary8 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-2-1")
            .name("PPK User 1 KSC 2")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .description("PPK User 1 KSC 2")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary10 =
        WorkbasketBuilder.newWorkbasket()
            .key("TPK_VIP")
            .name("Themenpostkorb VIP")
            .domain("DOMAIN_A")
            .type(WorkbasketType.TOPIC)
            .description("Themenpostkorb VIP")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary4 =
        WorkbasketBuilder.newWorkbasket()
            .key("TEAMLEAD-1")
            .name("PPK Teamlead KSC 1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .description("PPK Teamlead KSC 1")
            .owner("")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary15 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-B-2")
            .name("PPK User 2 KSC 1 Domain B")
            .domain("DOMAIN_B")
            .type(WorkbasketType.PERSONAL)
            .description("PPK User 2 KSC 1 Domain B")
            .owner("user-1-2")
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary6.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary6.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary1.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary12.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary10.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary12.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary5.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary4.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary8.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary8.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    task003 =
        TaskBuilder.newTask()
            .created(created)
            .claimed(null)
            .completed(null)
            .modified(created)
            .received(null)
            .planned(due)
            .due(due)
            .name("Widerruf")
            .description("Widerruf")
            .note(null)
            .priority(2)
            .manualPriority(-1)
            .state(TaskState.READY)
            .businessProcessId("PI_0000000000003")
            .owner(null)
            .read(false)
            .transferred(false)
            .callbackInfo(null)
            .callbackState(CallbackState.valueOf("NONE"))
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task125 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task025 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task225 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task026 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary7)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task00 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary6)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    task001 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary6)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    task207 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary5)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    task002 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary6)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);

    task106 =
        TaskBuilder.newTask()
            .state(TaskState.COMPLETED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task004 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task005 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task006 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task041 =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary15)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task208 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary10)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task106 =
        TaskBuilder.newTask()
            .state(TaskState.COMPLETED)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary4)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task023 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task024 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary1)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketIdIsProvided() throws Exception {
    Task task = task003;
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), defaultWorkbasketSummary6.getId());

    Task transferredTask = taskService.getTask(task.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SetStateCorrectly_When_TransferringSingleTask() {
    List<Pair<String, TaskState>> testCases =
        List.of(
            Pair.of(task125.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task025.getId(), TaskState.READY),
            Pair.of(task225.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task026.getId(), TaskState.READY));

    ThrowingConsumer<Pair<String, TaskState>> test =
        p -> {
          String taskId = p.getLeft();
          TaskState expectedState = p.getRight();
          taskService.transfer(taskId, defaultWorkbasketSummary6.getId());
          Task result = taskService.getTask(taskId);
          assertThat(result.getState()).isEqualTo(expectedState);
          taskService.transfer(taskId, defaultWorkbasketSummary7.getId());
        };

    return DynamicTest.stream(
        testCases.iterator(), p -> "Expected state: " + p.getRight().name(), test);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @Test
  void should_SetStateCorrectly_When_BulkTranferringTasks() throws Exception {
    String readyForReview = task125.getId();
    String ready = task025.getId();
    String inReview = task225.getId();
    String claimed = task026.getId();
    List<String> taskIds = List.of(readyForReview, ready, inReview, claimed);

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary6.getId(), taskIds);

    assertThat(results.containsErrors()).isFalse();
    assertThat(taskService.getTask(readyForReview).getState())
        .isEqualTo(taskService.getTask(inReview).getState())
        .isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(taskService.getTask(ready).getState())
        .isEqualTo(taskService.getTask(claimed).getState())
        .isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @pro.taskana.testapi.security.WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TransferTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task task = taskService.getTask(task003.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), defaultWorkbasketSummary6.getId());

    Task transferredTask = taskService.getTask(task003.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    Task task = taskService.getTask(task003.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "USER-1-1", "DOMAIN_A");

    Task transferredTask = taskService.getTask(task003.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ChangeDomain_When_TransferringTaskToWorkbasketWithDifferentDomain() throws Exception {
    Task task = taskService.getTask(task00.getId());
    String domain1 = task.getDomain();

    Task transferredTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.getDomain()).isNotEqualTo(domain1);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorization() throws Exception {
    Task task = taskService.getTask(task001.getId());

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), defaultWorkbasketSummary5.getId());
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(
      user = "user-1-1",
      groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorizationAndIsMemeberOfTaskRouterRole()
      throws Exception {
    Task task = taskService.getTask(task001.getId());

    assertThatThrownBy(() -> taskService.transfer(task.getId(), defaultWorkbasketSummary5.getId()))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_DestinationWorkbasketDoesNotExist() throws Exception {
    Task task = taskService.getTask(task003.getId());
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    ThrowingCallable call = () -> taskService.transfer(task.getId(), "INVALID");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferDoesNotExist() {
    ThrowingCallable call =
        () -> taskService.transfer("Invalid", defaultWorkbasketSummary5.getId());
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TransferWithNoTransferAuthorization() {
    ThrowingCallable call =
        () -> taskService.transfer(task207.getId(), defaultWorkbasketSummary1.getId());
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'teamlead-1' has no '[TRANSFER]' permission(s) "
                + "for Workbasket '"
                + defaultWorkbasketSummary5.getId()
                + "'.");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferWithNoAppendAuthorization() throws Exception {
    Task task = taskService.getTask(task002.getId());

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

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferIsAlreadyCompleted() throws Exception {
    Task task = taskService.getTask(task106.getId());

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), defaultWorkbasketSummary5.getId());
    assertThatThrownBy(call).isInstanceOf(InvalidTaskStateException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketIdIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList = List.of(task004.getId(), task005.getId());

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary6.getId(), taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    Task transferredTask = taskService.getTask(task004.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask(task005.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferOnlyValidTasks_When_SomeTasksToTransferCauseExceptions()
      throws Exception {
    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    // we can't use List.of because of the null value we insert
    List<String> taskIdList =
        Arrays.asList(
            task006.getId(), // working
            task041.getId(), // NotAuthorized READ
            task041.getId(), // NotAuthorized READ
            task208.getId(), // NotAuthorized TRANSFER
            "", // InvalidArgument
            null, // InvalidArgument
            "TKI:000000000000000000000000000000000099", // not existing
            task106.getId()); // already completed

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks(defaultWorkbasketSummary6.getId(), taskIdList);
    // check for exceptions in bulk
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().values()).hasSize(6);
    assertThat(results.getErrorForId(task041.getId()).getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId(task208.getId()).getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000099").getClass())
        .isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId(task106.getId()).getClass())
        .isEqualTo(InvalidTaskStateException.class);
    assertThat(results.getErrorForId("").getClass()).isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId(null).getClass()).isEqualTo(TaskNotFoundException.class);

    // verify valid requests
    Task transferredTask = taskService.getTask(task006.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();

    transferredTask = taskService.getTask(task208.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TPK_VIP");

    transferredTask = taskService.getTask(task106.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-1");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_BulkTransferTasksWithoutAppendPermissionOnTarget() {

    List<String> taskIdList =
        List.of(
            task006.getId(), // working
            task041.getId()); // NotAuthorized READ

    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary10.getId(), taskIdList);
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .hasMessageContaining("APPEND");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTasks_When_TransferringTasksWithListNotSupportingRemove() {
    List<String> taskIds = List.of(task006.getId());

    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary6.getId(), taskIds);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferredTaskListIsNull() {
    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary6.getId(), null);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not be null or empty.");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringEmptyTaskIdList() {
    ThrowingCallable call =
        () -> taskService.transferTasks(defaultWorkbasketSummary6.getId(), Collections.emptyList());
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList = List.of(task023.getId(), task024.getId());

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("GPK_B_KSC_1", "DOMAIN_B", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("GPK_B_KSC_1", "DOMAIN_B");
    Task transferredTask = taskService.getTask(task023.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask(task024.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlag_When_SetTransferFlagNotRequested() throws Exception {
    taskService.transfer(task003.getId(), defaultWorkbasketSummary6.getId(), false);

    Task transferredTask = taskService.getTask(task003.getId());
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlagWithinBulkTransfer_When_SetTransferFlagNotRequested()
      throws Exception {
    taskService.transferTasks(
        defaultWorkbasketSummary6.getId(),
        List.of(task003.getId(), task004.getId(), task005.getId()),
        false);

    List<TaskSummary> transferredTasks =
        taskService
            .createTaskQuery()
            .idIn(task004.getId(), task005.getId(), task003.getId())
            .list();

    assertThat(transferredTasks).extracting(TaskSummary::isTransferred).containsOnly(false);
  }
}
