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
  Task task1;
  Task task2;
  Task task3;
  Task task4;
  Task task5;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
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
    defaultWorkbasketSummary =
        WorkbasketBuilder.newWorkbasket()
            .key("GPK_KSC")
            .domain("DOMAIN_A")
            .description("Gruppenpostkorb KSC 2")
            .name("Gruppenpostkorb KSC 2")
            .type(WorkbasketType.GROUP)
            .owner(null)
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);
    defaultWorkbasketSummary2 =
        WorkbasketBuilder.newWorkbasket()
            .key("USER-1-1")
            .domain("DOMAIN_A")
            .description("Gruppenpostkorb KSC 2")
            .name("Gruppenpostkorb KSC 2")
            .type(WorkbasketType.PERSONAL)
            .owner(null)
            .markedForDeletion(false)
            .buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("teamlead-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);
    task1 =
        TaskBuilder.newTask()
            .externalId("ETI:000000000000000000000000000000000003")
            .claimed(null)
            .completed(null)
            .created(created)
            .modified(created)
            .due(due)
            .planned(due)
            .received(null)
            .name("Widerruf")
            .businessProcessId("PI_0000000000003")
            .transferred(false)
            .note(null)
            .priority(2)
            .manualPriority(-1)
            .callbackInfo(null)
            .callbackState(CallbackState.valueOf("NONE"))
            .owner(null)
            .read(false)
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task2 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task3 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task4 =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task5 =
        TaskBuilder.newTask()
            .state(TaskState.READY)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultTestObjectReference().build())
            .buildAndStore(taskService);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketIdIsProvided() throws Exception {
    Task task = task1;
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), defaultWorkbasketSummary.getId());

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
            Pair.of(task2.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task3.getId(), TaskState.READY),
            Pair.of(task4.getId(), TaskState.READY_FOR_REVIEW),
            Pair.of(task5.getId(), TaskState.READY));

    ThrowingConsumer<Pair<String, TaskState>> test =
        p -> {
          String taskId = p.getLeft();
          TaskState expectedState = p.getRight();
          taskService.transfer(taskId, "WBI:100000000000000000000000000000000006");
          Task result = taskService.getTask(taskId);
          assertThat(result.getState()).isEqualTo(expectedState);
          taskService.transfer(taskId, "WBI:100000000000000000000000000000000007");
        };

    return DynamicTest.stream(
        testCases.iterator(), p -> "Expected state: " + p.getRight().name(), test);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @Test
  void should_SetStateCorrectly_When_BulkTranferringTasks() throws Exception {
    String readyForReview = "TKI:100000000000000000000000000000000025";
    String ready = "TKI:000000000000000000000000000000000025";
    String inReview = "TKI:200000000000000000000000000000000025";
    String claimed = "TKI:000000000000000000000000000000000026";
    List<String> taskIds = List.of(readyForReview, ready, inReview, claimed);

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

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
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000006");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "USER-1-1", "DOMAIN_A");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ChangeDomain_When_TransferringTaskToWorkbasketWithDifferentDomain() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    String domain1 = task.getDomain();

    Task transferredTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.getDomain()).isNotEqualTo(domain1);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorization() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(
      user = "user-1-1",
      groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorizationAndIsMemeberOfTaskRouterRole()
      throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    assertThatThrownBy(
            () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005"))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_DestinationWorkbasketDoesNotExist() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    ThrowingCallable call = () -> taskService.transfer(task.getId(), "INVALID");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferDoesNotExist() {
    ThrowingCallable call =
        () -> taskService.transfer("Invalid", "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TransferWithNoTransferAuthorization() {
    ThrowingCallable call =
        () ->
            taskService.transfer(
                "TKI:200000000000000000000000000000000007",
                "WBI:100000000000000000000000000000000001");
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'teamlead-1' has no '[TRANSFER]' permission(s) "
                + "for Workbasket 'WBI:100000000000000000000000000000000005'.");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferWithNoAppendAuthorization() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000008");
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'user-1-1' has no '[APPEND]' permission(s) "
                + "for Workbasket 'WBI:100000000000000000000000000000000008'.");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferIsAlreadyCompleted() throws Exception {
    Task task = taskService.getTask("TKI:100000000000000000000000000000000006");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(InvalidTaskStateException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketIdIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000004", "TKI:000000000000000000000000000000000005");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000004");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000005");
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
            "TKI:000000000000000000000000000000000006", // working
            "TKI:000000000000000000000000000000000041", // NotAuthorized READ
            "TKI:000000000000000000000000000000000041", // NotAuthorized READ
            "TKI:200000000000000000000000000000000008", // NotAuthorized TRANSFER
            "", // InvalidArgument
            null, // InvalidArgument
            "TKI:000000000000000000000000000000000099", // not existing
            "TKI:100000000000000000000000000000000006"); // already completed

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    // check for exceptions in bulk
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().values()).hasSize(6);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000041").getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId("TKI:200000000000000000000000000000000008").getClass())
        .isEqualTo(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000099").getClass())
        .isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId("TKI:100000000000000000000000000000000006").getClass())
        .isEqualTo(InvalidTaskStateException.class);
    assertThat(results.getErrorForId("").getClass()).isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId(null).getClass()).isEqualTo(TaskNotFoundException.class);

    // verify valid requests
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();

    transferredTask = taskService.getTask("TKI:200000000000000000000000000000000008");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TPK_VIP");

    transferredTask = taskService.getTask("TKI:100000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-1");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_BulkTransferTasksWithoutAppendPermissionOnTarget() {

    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000006", // working
            "TKI:000000000000000000000000000000000041"); // NotAuthorized READ

    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000010", taskIdList);
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class)
        .hasMessageContaining("APPEND");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTasks_When_TransferringTasksWithListNotSupportingRemove() {
    List<String> taskIds = List.of("TKI:000000000000000000000000000000000006");

    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferredTaskListIsNull() {
    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", null);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not be null or empty.");
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringEmptyTaskIdList() {
    ThrowingCallable call =
        () ->
            taskService.transferTasks(
                "WBI:100000000000000000000000000000000006", Collections.emptyList());
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @pro.taskana.testapi.security.WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000023", "TKI:000000000000000000000000000000000024");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("GPK_B_KSC_1", "DOMAIN_B", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("GPK_B_KSC_1", "DOMAIN_B");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000023");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000024");
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
    taskService.transfer(
        "TKI:000000000000000000000000000000000003",
        "WBI:100000000000000000000000000000000006",
        false);

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
  }

  @pro.taskana.testapi.security.WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlagWithinBulkTransfer_When_SetTransferFlagNotRequested()
      throws Exception {
    taskService.transferTasks(
        "WBI:100000000000000000000000000000000006",
        List.of(
            "TKI:000000000000000000000000000000000003",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000005"),
        false);

    List<TaskSummary> transferredTasks =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000003")
            .list();

    assertThat(transferredTasks).extracting(TaskSummary::isTransferred).containsOnly(false);
  }
}
