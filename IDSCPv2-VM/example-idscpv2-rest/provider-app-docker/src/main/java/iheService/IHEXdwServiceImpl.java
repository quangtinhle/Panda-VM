package iheService;

import de.fraunhofer.isst.health.ihe.commons.domain.Code;
import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.xds.api.RegistryService;
import de.fraunhofer.isst.health.ihe.xds.api.RepositoryService;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xds.api.domain.ExtendedCompositeId;
import de.fraunhofer.isst.health.ihe.xds.api.domain.SubmissionSet;
import de.fraunhofer.isst.health.ihe.xds.api.domain.author.Author;
import de.fraunhofer.isst.health.ihe.xds.api.domain.status.AvailabilityStatus;
import de.fraunhofer.isst.health.ihe.xdw.services.ValidationException;
import de.fraunhofer.isst.health.ihe.xdw.services.XdwDocument;
import exception.XdwDocumentNotFoundException;
import org.apache.commons.io.IOUtils;

import javax.activation.DataSource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class IHEXdwServiceImpl implements IHEXdwService {

    private RegistryService iheRegistryService;
    private RepositoryService iheRepositoryService;
    public IHEXdwServiceImpl() {
    }

    @Inject
    public IHEXdwServiceImpl(
            RegistryService iheRegistryService,
            RepositoryService iheRepositoryService) {
        this.iheRegistryService = iheRegistryService;
        this.iheRepositoryService = iheRepositoryService;
    }

    @Override
    public SubmissionSet getSubmissionSet() {
        SubmissionSet submissionSet = new SubmissionSet();
        submissionSet.setEntryUUID("SubmissionSet1");
        submissionSet.setUniqueId(UUID.randomUUID().toString());
        submissionSet.setSourceId(UUID.randomUUID().toString());
        submissionSet.setContentTypeCode(getTestCode("ContentTypeCode"));
        submissionSet.setSubmissionTime(ZonedDateTime.now());
        submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        submissionSet.setEntryUUID(UUID.randomUUID().toString());
        submissionSet.setContentTypeCode(getTestCode("ContentTypeCode"));
        submissionSet.setTitle("Smith Submission");
        submissionSet.setHomeCommunityId("1.3.6.1.4.1.21367.13.20.3000");
        submissionSet.setPatientId(getID());
        return submissionSet;
    }

    @Override
    public DocumentEntry getXdwDocumentEntry(Code classCode, String workflowInstanceId) {
        Author author = new Author();
        author.setFirstName("");
        author.setLastName("");
        author.setOrganizationIdentifier(UUID.randomUUID().toString());

        ExtendedCompositeId extendedCompositeId = new ExtendedCompositeId();
        extendedCompositeId.setIdentifierTypeCode(WORKFLOW_INSTANCE_ID_TYPE_CODE);
        extendedCompositeId.setIdNumber(workflowInstanceId);
        List<ExtendedCompositeId> referenceIdList = new ArrayList<>();
        referenceIdList.add(extendedCompositeId);

        ID patientID = PATIENT_ID;
        DocumentEntry documentEntry = new DocumentEntry();
        documentEntry.setReferencedIdList(referenceIdList);
        documentEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        documentEntry.setAuthor(author);
        documentEntry.setTitle("XdwDocument");
        documentEntry.setEntryUUID("urn:uuid:" + UUID.randomUUID().toString());
        documentEntry.setUniqueId("2.25." + System.currentTimeMillis());
        documentEntry.setMimeType("MimeType");
        documentEntry.setClassCode(classCode);
        documentEntry.setTypeCode(getTestCode("TypeCode"));
        documentEntry.setFormatCode(getTestCode("FormatCode"));
        documentEntry.setConfidentialityCode(XdsMetaDataHelper.getCode("1.2.276.0.76.11.33",
                "IHEXDSconfidentialityCode", "N", "normal"));
        documentEntry.setHealthcareFacilityTypeCode(getTestCode("HealthcareFacilityTypeCode"));
        documentEntry.setPracticeSettingCode(getTestCode("PracticeSettingCode"));
        documentEntry.setHomeCommunityId("1.3.6.1.4.1.21367.13.20.3000");
        documentEntry.setCreationTime(ZonedDateTime.now());
        documentEntry.setPatientId(patientID);
        documentEntry.setLanguageCode("de");
        documentEntry.setSourcePatientId(patientID);
        return documentEntry;
    }

    @Override
    public DataSource getXdwDataSource(XdwDocument xdwDocument) throws ValidationException {
        //InputStream xmlInputStream = IOUtils.toInputStream(xdwDocument.getXmlString(), StandardCharsets.UTF_8);
        //InputStream xdwDocumentInputStream = xdwDocument.getInputStream(StandardCharsets.UTF_8);
        //InputStream inputStream = new ByteArrayInputStream(xdwDocument.getXmlString().getBytes());
        String content = xdwDocument.getXmlString();
        DataSource xdwDocumentDataSource = new DataSource() {
            @Override
            public InputStream getInputStream() {


                //return xdwDocumentInputStream;
                //return inputStream;
                //return xmlInputStream;
                return new ByteArrayInputStream(content.getBytes());
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return "application/xml";
            }

            @Override
            public String getName() {
                return "XDWWorkflowDocument";
            }
        };
        return xdwDocumentDataSource;
    }

   @Override
    public DataSource getStringSource(String json) {
        InputStream jsonInputStream = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
        DataSource jsonDataSource = new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return jsonInputStream;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return "application/json";
            }

            @Override
            public String getName() {
                return "JSON Document";
            }
        };
        return jsonDataSource;
    }

    @Override
    public Code getXdwDocumentClassCode() {
        Code classCode = new Code();
        classCode.setCodesystemName(IHEXdwService.XDW_DOCUMENT_CLASSCODE_CODESYSTEM_NAME);
        classCode.setCodesystem(IHEXdwService.XDW_DOCUMENT_CLASSCODE_CODESYSTEM);
        classCode.setDisplayname(IHEXdwService.XDW_DOCUMENT_CLASSCODE_DISPLAYNAME);
        classCode.setValue(IHEXdwService.XDW_DOCUMENT_CLASSCODE_VALUE);
        return classCode;
    }

    @Override
    public Code getFormInputDocumentClassCode() {
        Code classCode = new Code();
        classCode.setCodesystemName(IHEXdwService.XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME);
        classCode.setCodesystem(IHEXdwService.XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM);
        classCode.setDisplayname(IHEXdwService.XDW_FORM_INPUT_DOCUMENT_CLASSCODE_DISPLAYNAME);
        classCode.setValue(IHEXdwService.XDW_FORM_INPUT_DOCUMENT_CLASSCODE_VALUE);
        return classCode;
    }

    @Override
    public Code getFormOutputDocumentClassCode() {
        Code classCode = new Code();
        classCode.setCodesystemName(IHEXdwService.XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME);
        classCode.setCodesystem(IHEXdwService.XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM);
        classCode.setDisplayname(IHEXdwService.XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_DISPLAYNAME);
        classCode.setValue(IHEXdwService.XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_VALUE);
        return classCode;
    }

    @Override
    public XdwDocument getXdwDocumentForDocumentEntry(DocumentEntry xdwDocumentEntry) throws IOException, ValidationException {
        String currentXdwDocumentUniqueId = xdwDocumentEntry.getUniqueId();
        InputStream documentContent = iheRepositoryService.getDocumentContent(
                currentXdwDocumentUniqueId,
                xdwDocumentEntry.getRepositoryUniqueId());
        String jsonContent = IOUtils.toString(documentContent, StandardCharsets.UTF_8);
        System.out.println("Received ihe xdw document content: " + jsonContent);

        return XdwDocument.fromXml(jsonContent, StandardCharsets.UTF_8);
    }

    @Override
    public DocumentEntry getCurrentXdwDocumentDocumentEntry(String workflowInstanceId) throws XdwDocumentNotFoundException {
        List<DocumentEntry> documentEntryList = iheRegistryService.findDocumentsForPatient(
                IHEXdwService.PATIENT_ID,
                AvailabilityStatus.APPROVED,
                false);
        List<DocumentEntry> xdwDocumentEntries = documentEntryList.stream()
                .filter(documentEntry -> AvailabilityStatus.APPROVED.equals(documentEntry.getAvailabilityStatus()))
                .filter(this::isXdwDocument)
                .filter(documentEntry -> this.documentEntryContainsWorkflow(documentEntry, workflowInstanceId))
                .collect(Collectors.toList());

        if (xdwDocumentEntries.isEmpty()) {
            throw new XdwDocumentNotFoundException("No active xdw document has been found");
        }
        if (xdwDocumentEntries.size() > 1) {
            throw new XdwDocumentNotFoundException(
                    "There are more than one active xdw documents for workflowId: "
                            + workflowInstanceId);
        }
        return xdwDocumentEntries.get(0);
    }

    private boolean isXdwDocument(DocumentEntry documentEntry) {
        String xdwClassCode = getXdwDocumentClassCode().getValue();
        String currentDocumentClassCode = documentEntry.getClassCode().getValue();
        return xdwClassCode.equals(currentDocumentClassCode);
    }

    private boolean documentEntryContainsWorkflow(DocumentEntry documentEntry, String workflowInstanceId) {
        List<ExtendedCompositeId> matchingWorkflowsList = documentEntry.getReferencedIdList().stream()
                .filter(extendedCompositeId -> {
                    boolean workflowInstanceIdCodeType = IHEXdwService.WORKFLOW_INSTANCE_ID_TYPE_CODE
                            .equalsIgnoreCase(extendedCompositeId.getIdentifierTypeCode());
                    boolean instanceIdMatch = workflowInstanceId.equalsIgnoreCase(extendedCompositeId.getIdNumber());
                    return workflowInstanceIdCodeType && instanceIdMatch;
                })
                .collect(Collectors.toList());
        return !matchingWorkflowsList.isEmpty();
    }

    private Code getTestCode(String value) {
        Code code = new Code();
        code.setCodesystem(UUID.randomUUID().toString());
        code.setCodesystemName("Name");
        code.setDisplayname(value);
        code.setValue(UUID.randomUUID().toString());
        return code;
    }

    private ID getID() {
        ID id = new ID();
        id.setRoot(UUID.randomUUID().toString());
        id.setExtension(UUID.randomUUID().toString());
        return id;
    }
}
