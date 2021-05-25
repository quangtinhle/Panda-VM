package mockupData;

import de.fraunhofer.isst.health.ihe.commons.domain.Code;
import de.fraunhofer.isst.health.ihe.xds.api.RegistryService;
import de.fraunhofer.isst.health.ihe.xds.api.RepositoryService;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xds.api.domain.SubmissionSet;
import de.fraunhofer.isst.health.ihe.xds.api.exceptions.XDSApiException;
import de.fraunhofer.isst.health.ihe.xdw.services.ValidationException;
import de.fraunhofer.isst.health.ihe.xdw.services.XdwDocument;
import exception.IHEOutOfDateException;
import exception.XdwDocumentNotFoundException;
import iheService.IHEXdwService;
import iheService.IHEXdwServiceImpl;
import propertyService.PropertyService;
import propertyService.PropertyServiceImpl;
import registryService.RegistryServiceImpl;
import repositoryService.RepositoryServiceImpl;

import javax.activation.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;

public class IHEAdapter {
    private String taskProcessInstanceId;
    private RegistryService registryService;
    private RepositoryService repositoryService;
    private IHEXdwService iheXdwService;
    private MockupHelper mockupHelper;
    private static final Logger LOGGER = Logger.getLogger(IHEAdapter.class.getName());

    public RegistryService getRegistryService() {
        return this.registryService;
    }

    public RepositoryService getRepositoryService() {
        return this.repositoryService;
    }


    public IHEAdapter() {
        taskProcessInstanceId = UUID.randomUUID().toString();
        initHelpers();
    }

    private void initHelpers() {
        //registryService = new CamundaIHERegistryService();
        //repositoryService = new CamundaIHERepositoryService();
        registryService = new RegistryServiceImpl();
        repositoryService = new RepositoryServiceImpl();
        iheXdwService = new IHEXdwServiceImpl(registryService, repositoryService);
        mockupHelper = new MockupHelper(this);
    }

    public String createDocumentAndReturnEntryUuid() throws Exception {
        XdwDocument xdwDocument = getMockupXdwDocument();
        DocumentEntry workflowDocumentEntry = getXdwDocumentEntry();

        provideDocument(
                workflowDocumentEntry,
                getXdwDataSource(xdwDocument),
                getSubmissionSet());

        return workflowDocumentEntry.getEntryUUID();
    }

    public String appendDocument(DocumentEntry oldXdwDocumentEntry) throws Exception {
        XdwDocument xdwDocument = getXdwToReplaceWith(oldXdwDocumentEntry);
        DocumentEntry workflowDocumentEntry = getXdwDocumentEntry();

        try {
            replaceDocument(
                    oldXdwDocumentEntry.getEntryUUID(),
                    workflowDocumentEntry,
                    xdwDocument);
            return workflowDocumentEntry.getEntryUUID();
        } catch (IHEOutOfDateException e) {
            return retryAppendDocument(oldXdwDocumentEntry, workflowDocumentEntry);
        }
    }

    public XdwDocument getMockupXdwDocument() throws ValidationException, XDSApiException {
        return mockupHelper.getMockupXdwDocument();
    }

    private String retryAppendDocument(DocumentEntry oldXdwDocumentEntry, DocumentEntry workflowDocumentEntry) throws Exception {
        String errorMessage = "Replacement of entry UUID " + oldXdwDocumentEntry.getEntryUUID()
                + " was out of date, reloading new XDW and trying again...";
        LOGGER.warning(errorMessage);
        return appendDocument(workflowDocumentEntry);
    }

    public DocumentEntry provideDocument(DocumentEntry documentEntry, DataSource dataSource, SubmissionSet submissionSet)
            throws XDSApiException {
        return repositoryService.provideDocument(documentEntry, dataSource, submissionSet);
    }

    public XdwDocument getXdwToReplaceWith(DocumentEntry oldXdwDocumentEntry) throws Exception {
        return mockupHelper.getXdwToReplaceWith(oldXdwDocumentEntry);
    }

    public DocumentEntry getXdwDocumentEntry() {
        return iheXdwService.getXdwDocumentEntry(getXdwDocumentClassCode(), taskProcessInstanceId);
    }

    public synchronized void replaceDocument(String entryUUID, DocumentEntry workflowDocumentEntry, XdwDocument xdwDocument)
            throws ValidationException, IHEOutOfDateException {
        repositoryService.replaceDocument(entryUUID, workflowDocumentEntry, getXdwDataSource(xdwDocument), getSubmissionSet());
    }

    XdwDocument getXdwDocumentForDocumentEntry(DocumentEntry xdwDocumentEntry) throws IOException, ValidationException {
        return iheXdwService.getXdwDocumentForDocumentEntry(xdwDocumentEntry);
    }

    Code getFormInputDocumentClassCode() {
        return iheXdwService.getFormInputDocumentClassCode();
    }

    DocumentEntry getXdwDocumentEntry(Code classCode) {
        return iheXdwService.getXdwDocumentEntry(classCode, taskProcessInstanceId);
    }

    public DocumentEntry getDocument(String entryUuid) {
        return registryService.getDocument(entryUuid);
    }

    InputStream getDocumentContent(String uniqueId, String repositoryOid) throws FileNotFoundException {
        return repositoryService.getDocumentContent(uniqueId, repositoryOid);
    }

    DataSource getStringSource(String json) {
        return iheXdwService.getStringSource(json);
    }

    public SubmissionSet getSubmissionSet() {
        return iheXdwService.getSubmissionSet();
    }

    private Code getXdwDocumentClassCode() {
        return iheXdwService.getXdwDocumentClassCode();
    }

    public DataSource getXdwDataSource(XdwDocument xdwDocument) throws ValidationException {
        return iheXdwService.getXdwDataSource(xdwDocument);
    }

    public XdwDocument getIHEXdwDocument(String entryUUID) throws IOException, ValidationException {
        return mockupHelper.getIHEXdwDocument(entryUUID);
    }

    public DocumentEntry getXdwWithoutReference() throws XdwDocumentNotFoundException, XDSApiException {
        return getCurrentXdwDocumentDocumentEntry(taskProcessInstanceId);
    }

    public DocumentEntry getCurrentXdwDocumentDocumentEntry(String taskProcessInstanceId)
            throws XdwDocumentNotFoundException, XDSApiException {
        return iheXdwService.getCurrentXdwDocumentDocumentEntry(taskProcessInstanceId);
    }
}
