package iheService;

import de.fraunhofer.isst.health.ihe.commons.domain.Code;
import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xds.api.domain.SubmissionSet;
import de.fraunhofer.isst.health.ihe.xds.api.exceptions.XDSApiException;
import de.fraunhofer.isst.health.ihe.xdw.services.ValidationException;
import de.fraunhofer.isst.health.ihe.xdw.services.XdwDocument;
import exception.XdwDocumentNotFoundException;

import javax.activation.DataSource;
import java.io.IOException;

/**
 * Provides IHE objects used in the context of XDW
 */
public interface IHEXdwService {


    ID PATIENT_ID = new ID("1.3.6.1.4.1.21367.13.20.3001", "0962");
    String WORKFLOW_INSTANCE_ID_TYPE_CODE = "WORKFLOW_INSTANCE_ID_TYPE_CODE";

    String XDW_DOCUMENT_CLASSCODE_CODESYSTEM_NAME = "XDW_DOCUMENT_CLASSCODE_CODESYSTEM_NAME";
    String XDW_DOCUMENT_CLASSCODE_CODESYSTEM = "XDW_DOCUMENT_CLASSCODE_CODESYSTEM";
    String XDW_DOCUMENT_CLASSCODE_DISPLAYNAME = "XDW_DOCUMENT_CLASSCODE_DISPLAYNAME";
    String XDW_DOCUMENT_CLASSCODE_VALUE = "XDW_DOCUMENT_CLASSCODE_VALUE";

    String XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME = "XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME";
    String XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM = "XDW_FORM_INPUT_DOCUMENT_CLASSCODE_CODESYSTEM";
    String XDW_FORM_INPUT_DOCUMENT_CLASSCODE_DISPLAYNAME = "XDW_FORM_INPUT_DOCUMENT_CLASSCODE_DISPLAYNAME";
    String XDW_FORM_INPUT_DOCUMENT_CLASSCODE_VALUE = "XDW_FORM_INPUT_DOCUMENT_CLASSCODE_VALUE";

    String XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME = "XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM_NAME";
    String XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM = "XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_CODESYSTEM";
    String XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_DISPLAYNAME = "XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_DISPLAYNAME";
    String XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_VALUE = "XDW_FORM_OUTPUT_DOCUMENT_CLASSCODE_VALUE";

    SubmissionSet getSubmissionSet();

    DocumentEntry getXdwDocumentEntry(Code classCode, String workflowInstanceId);


    DataSource getXdwDataSource(XdwDocument xdwDocument) throws ValidationException;


    DataSource getStringSource(String json);

    Code getXdwDocumentClassCode();

    Code getFormInputDocumentClassCode();

    Code getFormOutputDocumentClassCode();

    /**
     * Retrieves the XDW-Document for the xdwDocumentEntry and parses it into a object representation
     * @param xdwDocumentEntry a documentEntry retrieved from the IHE-registry
     * @return The XDW-Document
     * @throws IOException
     * @throws ValidationException
     */
    XdwDocument getXdwDocumentForDocumentEntry(
            DocumentEntry xdwDocumentEntry)
            throws IOException, ValidationException;

    /**
     * Finds the latest APPROVED XDW-Document for the workflowInstanceId
     * @param workflowInstanceId The workflowInstanceId of the corresponding camunda process instance
     * @return A DocumentEntry pointing to the XDW-Document
     * @throws XDSApiException
     * @throws XdwDocumentNotFoundException
     */
    DocumentEntry getCurrentXdwDocumentDocumentEntry(
            String workflowInstanceId)
            throws XDSApiException, XdwDocumentNotFoundException;
}