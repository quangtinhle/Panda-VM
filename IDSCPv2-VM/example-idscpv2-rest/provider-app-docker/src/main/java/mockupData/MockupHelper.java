package mockupData;

import de.fraunhofer.isst.health.ihe.commons.domain.Code;
import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xds.api.exceptions.XDSApiException;
import de.fraunhofer.isst.health.ihe.xdw.services.*;
import de.fraunhofer.isst.health.ihe.xdw.services.service.XdwService;
import de.fraunhofer.isst.health.ihe.xdw.services.service.XdwServiceImpl;
import de.fraunhofer.isst.health.ihe.xdw.services.task.XdwTask;
import de.fraunhofer.isst.health.ihe.xdw.services.task.XdwTaskBuilderImpl;
import de.fraunhofer.isst.health.ihe.xdw.services.task.attachment.XdwAttachment;
import de.fraunhofer.isst.health.ihe.xdw.services.task.attachment.XdwAttachmentBuilderImpl;
import de.fraunhofer.isst.health.ihe.xdw.services.task.taskdetails.XdwTaskDetails;
import de.fraunhofer.isst.health.ihe.xdw.services.task.taskdetails.XdwTaskDetailsBuilderImpl;
import de.fraunhofer.isst.health.ihe.xdw.services.task.taskevent.XdwTaskEvent;
import de.fraunhofer.isst.health.ihe.xdw.services.task.taskevent.XdwTaskEventBuilder;
import de.fraunhofer.isst.health.ihe.xdw.services.task.taskevent.XdwTaskEventBuilderImpl;
import de.fraunhofer.isst.health.ihe.xdw.services.workflowstatushistory.XdwDocumentEvent;
import iheService.IHEXdwService;
import org.apache.commons.io.IOUtils;
import org.oasis_open.docs.ns.bpel4people.ws_humantask.types._200803.TTaskEventType;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class MockupHelper {
    private static final String testBusinessKey = "1.3.0.4.0.4";
    private static final String formFieldJson =
            "[{\"id\":\"request\",\"label\":\"Antrag\",\"typeName\":\"string\",\"value\":{\"value\":null}}," +
                    "{\"id\":\"reason\",\"label\":\"Begründung\",\"typeName\":\"string\",\"value\":{\"value\":null}}]";
    private final IHEAdapter iheAdapter;

    public MockupHelper(IHEAdapter iheAdapter) {
        this.iheAdapter = iheAdapter;
    }

    private XdwTask getXdwTaskForCamundaTask(String variableInputDocumentId) throws ValidationException {

        List<String> potentialOwners = new ArrayList<>();
        potentialOwners.add("ScientistA");

        XdwTaskDetails xdwTaskDetails = new XdwTaskDetailsBuilderImpl()
                .withId(UUID.randomUUID().toString())
                .withName("delegateTaskTestName")
                .withStatus(XdwTaskEventBuilder.Status.CREATED)
                .withTaskType("Requested")
                .withActualOwner("Owner")
                .withPotentialOwners(potentialOwners)
                .withCreatedTime(ZonedDateTime.now())
                .withCreatedBy("Creator")
                .withLastModifiedTime(ZonedDateTime.now())
                .build();

        XdwTaskEvent xdwTaskEvent = new XdwTaskEventBuilderImpl()
                .withEventTime(ZonedDateTime.now())
                .withEventType(TTaskEventType.CREATE)
                .withId(BigInteger.valueOf(1))
                .withStatus(XdwTaskEventBuilder.Status.CREATED)
                .withIdentifier(UUID.randomUUID().toString())
                .build();

        XdwTask xdwTask = new XdwTaskBuilderImpl()
                .withDescription("DescriptionMockupTest")
                .withInput(getVariableInputAttachment(variableInputDocumentId))
                .withOutput(new ArrayList<>())
                .withTaskDetails(xdwTaskDetails)
                .addTaskEvent(xdwTaskEvent)
                .build();
        return xdwTask;
    }

    private List<XdwAttachment> getVariableInputAttachment(String variableInputDocumentId) throws ValidationException {
        XdwAttachment xdwAttachment = new XdwAttachmentBuilderImpl()
                .withAccessType(XdwAttachment.AccessType.XDSRegistered)
                .withAttachedTime(ZonedDateTime.now())
                .withName("Variable-Input-Document-for-Xdw-Document")
                .withIdentifier(variableInputDocumentId)
                .withContentType("application/json")
                .withAttachedBy("Camunda-Engine")
                .build();
        List<XdwAttachment> xdwAttachmentList = new ArrayList<>();
        xdwAttachmentList.add(xdwAttachment);
        return xdwAttachmentList;
    }

    private ID getID() {
        ID id = new ID();
        id.setRoot(UUID.randomUUID().toString());
        id.setExtension(UUID.randomUUID().toString());
        return id;
    }

    private Code getTestCode(String value, String codeSystemName) {
        Code code = new Code();
        code.setDisplayname(value);
        code.setValue(UUID.randomUUID().toString());
        code.setCodesystem(UUID.randomUUID().toString());
        code.setCodesystemName(codeSystemName);
        return code;
    }

    private XdwPatient getXdwPatient() {
        XdwPatient xdwPatient = new XdwPatient(IHEXdwService.PATIENT_ID);
        return xdwPatient;
    }

    private XdwAuthor getAuthor() {
        XdwAuthor xdwAuthor = new XdwAuthor(getID());
        return xdwAuthor;
    }

    XdwDocument getXdwToReplaceWith(DocumentEntry xdwDocumentEntry) throws Exception {
        XdwDocument xdwDocument = iheAdapter.getXdwDocumentForDocumentEntry(xdwDocumentEntry);
        xdwDocument.getTaskList().add(getMockupXdwTask());
        xdwDocument.setWorkflowDocumentSequenceNumber(xdwDocument.getWorkflowDocumentSequenceNumber() + 1);
        return xdwDocument;
    }

    private DocumentEntry getXdwDocumentEntry() {
        Code classCode = iheAdapter.getFormInputDocumentClassCode();
        return iheAdapter.getXdwDocumentEntry(classCode);
    }

    XdwDocument getMockupXdwDocument() throws XDSApiException, ValidationException {
        XdwTask xdwTask = getMockupXdwTask();
        XdwDocument xdwDocument = createXdwDocument(xdwTask, testBusinessKey);
        return xdwDocument;
    }

    private XdwDocument createXdwDocument(XdwTask xdwTask, String workflowInstanceId) throws ValidationException {
        XdwTaskEvent firstTaskEvent = xdwTask.getTaskEventList().get(0);

        XdwService xdwService = new XdwServiceImpl();
        XdwDocumentEvent xdwDocumentEvent = xdwService.getXdwDocumentEventBuilder()
                .withEventTime(firstTaskEvent.getEventTime())
                .withEventType(TTaskEventType.CREATE)
                .withTaskEventIdentifier(firstTaskEvent.getIdentifier())
                .withAuthor("Camunda-Engine")
                .withPreviousStatus(XdwDocumentBuilder.WorkflowStatus.Unavailable)
                .withActualStatus(XdwDocumentBuilder.WorkflowStatus.OPEN)
                .build();

        XdwDocument xdwDocument = xdwService.getXdwDocumentBuilder()
                .withID(getID())
                .withWorkflowInstanceId(workflowInstanceId)
                .withConfidentialityCode(getTestCode("ConfidentialityCode", "name"))
                .withPatient(getXdwPatient())
                .withAuthor(getAuthor())
                .withWorkflowStatus(XdwDocumentBuilder.WorkflowStatus.OPEN)
                .withWorkflowDefinitionReference("dua")
                .addTask(xdwTask)
                .addDocumentEvent(xdwDocumentEvent)
                .build();
        return xdwDocument;
    }

    XdwDocument getIHEXdwDocument(String entryUuid) throws IOException, ValidationException {
        DocumentEntry document = iheAdapter.getDocument(entryUuid);
        InputStream documentContent = iheAdapter.getDocumentContent(document.getUniqueId(), document.getRepositoryUniqueId());
        String jsonContent = IOUtils.toString(documentContent, StandardCharsets.UTF_8);
        XdwDocument xdw = XdwDocument.fromXml(jsonContent, StandardCharsets.UTF_8);
        applySequenceNumberWorkaround(xdw, jsonContent);
        return xdw;
    }

    private XdwTask getMockupXdwTask() throws XDSApiException, ValidationException {
        String variableInputDocumentId = createVariableDocument();
        XdwTask xdwTask = getXdwTaskForCamundaTask(variableInputDocumentId);
        return xdwTask;
    }

    private String createVariableDocument() throws XDSApiException {
        DocumentEntry documentEntry = iheAdapter.provideDocument(
                getXdwDocumentEntry(),
                iheAdapter.getStringSource(loadResourceFile()),
                iheAdapter.getSubmissionSet());

        return documentEntry.getUniqueId() + "/" + documentEntry.getEntryUUID();
    }

    private String loadResourceFile() {
        Path path = null;
        try {
            path = Paths.get(getClass().getClassLoader().getResource("SMITH_Ausschnitt-Testdaten_DUP_2021-04-29.json").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<String> fileLines = null;
        try {
            fileLines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        fileLines.forEach(line -> result.append(line).append("\n"));
        return result.toString();
    }

    /**
     * XdwDocument.fromXml() ignored sequence number, this method re-adds it afterwards.
     */
    private void applySequenceNumberWorkaround(XdwDocument xdw, String jsonContent) {
        // todo
    }
}
