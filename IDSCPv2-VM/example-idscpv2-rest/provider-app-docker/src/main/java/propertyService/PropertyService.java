package propertyService;

/**
 * Gives access to all relevant configuration parameters
 */
public interface PropertyService {

    /**
     * @return The IHE repository URL used by the marketplace to retrieve documents
     */
    String getIHERepositoryUrl();

    /**
     * @return The IHE registry URL used by the marketplace to retrieve document metadata
     */
    String getIHERegistryUrl();

    /**
     * @return Defines the environment the marketplace is deployed to
     */
    String getSmithProfile();

    /**
     * @return The DSUB Listening Address where the marketplace listens for incoming feasibility request results
     */
    String getDSUBAddress();

    String getIHERepositoryId();

    /**
     * @return The configuration file used to define IHE metadata to be set in IHE documents
     */
    String getIHEXDSPropertyConfigurationFile();

    boolean isSendDuaDSUBNotification();

    boolean isDuaUserContext();

    boolean isDuaCheckOrganisation();

    boolean isShowWithdrawButton();

    String getIDPEndpointUrl();

    /**
     * @return The camunda REST endpoint address
     */
    String getCamundaRestEndpoint();

    /**
     * @return The IHE repository URL used by the Camunda-XDW-Extension to retrieve documents
     */
    String getCamundaIHERepositoryUrl();

    String getCamundaIHERegistryUrl();

    String getCamundaXdwDSUBListeningAddress();

    boolean isLogRequests();

    String getDSUBSubscribeEndpoint();

    /**
     * For testing purposes
     * @return Whether to create FeasibilityRequestResponseDocuments for test purposes or not
     */
    boolean isCreateFeasibilityResponseDocuments();

    /**
     * For testing purposes
     * @return Whether to create DSUB Notifications for FeasibilityResponses or not
     */
    boolean isSendFeasibilityResponseDSUB();

    /**
     * @return Name of resource file containing all available DIZ information, used in HPDService
     */
    String getDizCatalogueName();
}
