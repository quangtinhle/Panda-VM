package propertyService;

import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class PropertyServiceImpl implements PropertyService {

    public static final String SMITH_PROFILE_LOCAL = "local";
    public static final String SMITH_PROFILE_DEVELOPMENT = "development";
    public static final String SMITH_PROFILE_PRODUCTION = "production";

    private static final String IHE_REPOSITORY_URL = "ihe.repository.url";
    private static final String IHE_REPOSITORY_ID = "ihe.repository.id";
    private static final String IHE_REGISTRY_URL = "ihe.registry.url";
    private static final String SMITH_PROFILE = "smith.profile";
    private static final String SMITH_DUA_SEND_DSUB = "smith.dua.senddsub";
    private static final String SMITH_DUA_USER_CONTEXT = "smith.dua.usercontext";
    private static final String SMITH_DUA_CHECK_ORGANISATION = "smith.dua.checkorganisation";
    private static final String SMITH_DUA_SHOW_WITHDRAW_BUTTON = "smith.dua.showwithdrawbutton";
    private static final String DSUB_ADDRESS = "ihe.dsub.listener.address";
    private static final String XDS_PROPERTIES_CONFIGURATION_FILE = "ihe.xds.properties.configuration.filename";
    private static final String IDP_ENDPOINT_URL = "idp.url";
    private static final String CAMUNDA_REST_ENDPOINT = "camunda.rest.url";
    private static final String CAMUNDA_IHE_REGISTRY_URL = "camunda.ihe.registry.url";
    private static final String CAMUNDA_IHE_REPOSITORY_URL = "camunda.ihe.repository.url";
    private static final String CAMUNDA_XDW_DSUB_LISTENING_ADDRESS = "camunda.xdwextension.dsub.listener";
    private static final String LOG_REQUESTS = "ihe.log.requests";
    private static final String DSUB_SUBSCRIBE_ENDPOINT = "dsub.subscribe.endpoint";
    private static final String SMITH_FEASIBILITY_DOCUMENTS = "smith.feasibility.create.response.documents";
    private static final String SMITH_FEASIBILITY_DSUB = "smith.feasibility.create.response.dsub";
    private static final String DIZ_CATALOGUE = "diz.catalogue";

    private final Properties properties;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public PropertyServiceImpl() {
        try {
            String internalPropertyFilePath = "properties/smith.properties";
            InputStream resourceAsStream = PropertyServiceImpl.class.getClassLoader()
                    .getResourceAsStream(internalPropertyFilePath);

            if (resourceAsStream == null) {
                throw new RuntimeException("Could not find properties file: " + internalPropertyFilePath);
            }
            properties = new Properties();
            properties.load(resourceAsStream);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not read properties file", e);
            throw new RuntimeException("Could not read properties file", e);
        }
    }

    private String getPropertySafe(String key) {
        String property = properties.getProperty(key);
        if (property == null) {
            throw new RuntimeException("Could not find property: " + key);
        }
        return property;
    }

    @Override
    public String getIHERepositoryUrl() {
        return getPropertySafe(IHE_REPOSITORY_URL);
    }

    @Override
    public String getIHERegistryUrl() {
        return getPropertySafe(IHE_REGISTRY_URL);
    }

    @Override
    public String getSmithProfile() {
        return getPropertySafe(SMITH_PROFILE);
    }

    @Override
    public String getDSUBAddress() {
        return getPropertySafe(DSUB_ADDRESS);
    }

    @Override
    public String getIHERepositoryId() {
        return getPropertySafe(IHE_REPOSITORY_ID);
    }

    @Override
    public String getIHEXDSPropertyConfigurationFile() {
        return getPropertySafe(XDS_PROPERTIES_CONFIGURATION_FILE);
    }

    @Override
    public boolean isSendDuaDSUBNotification() {
        String sendDuaDSUBNotification = getPropertySafe(SMITH_DUA_SEND_DSUB);
        return "true".equalsIgnoreCase(sendDuaDSUBNotification);
    }

    @Override
    public boolean isDuaUserContext() {
        String sendDuaDSUBNotification = getPropertySafe(SMITH_DUA_USER_CONTEXT);
        return "true".equalsIgnoreCase(sendDuaDSUBNotification);
    }

    @Override
    public boolean isDuaCheckOrganisation() {
        String sendDuaDSUBNotification = getPropertySafe(SMITH_DUA_CHECK_ORGANISATION);
        return "true".equalsIgnoreCase(sendDuaDSUBNotification);
    }

    @Override
    public boolean isShowWithdrawButton() {
        String sendDuaDSUBNotification = getPropertySafe(SMITH_DUA_SHOW_WITHDRAW_BUTTON);
        return "true".equalsIgnoreCase(sendDuaDSUBNotification);
    }

    @Override
    public String getIDPEndpointUrl() {
        return getPropertySafe(IDP_ENDPOINT_URL);
    }

    @Override
    public String getCamundaRestEndpoint() {
        return getPropertySafe(CAMUNDA_REST_ENDPOINT);
    }

    @Override
    public String getCamundaIHERepositoryUrl() {
        return getPropertySafe(CAMUNDA_IHE_REPOSITORY_URL);
    }

    @Override
    public String getCamundaIHERegistryUrl() {
        return getPropertySafe(CAMUNDA_IHE_REGISTRY_URL);
    }

    @Override
    public String getCamundaXdwDSUBListeningAddress() {
        return getPropertySafe(CAMUNDA_XDW_DSUB_LISTENING_ADDRESS);
    }

    @Override
    public boolean isLogRequests() {
        String logRequests = getPropertySafe(LOG_REQUESTS);
        return "true".equalsIgnoreCase(logRequests);
    }

    @Override
    public String getDSUBSubscribeEndpoint() {
        return getPropertySafe(DSUB_SUBSCRIBE_ENDPOINT);
    }

    @Override
    public boolean isCreateFeasibilityResponseDocuments() {
        String createResponseDocuments = getPropertySafe(SMITH_FEASIBILITY_DOCUMENTS);
        return "true".equalsIgnoreCase(createResponseDocuments);
    }

    @Override
    public boolean isSendFeasibilityResponseDSUB() {
        String createDSUBNotifications = getPropertySafe(SMITH_FEASIBILITY_DSUB);
        return "true".equalsIgnoreCase(createDSUBNotifications);
    }

    @Override
    public String getDizCatalogueName() {
        return getPropertySafe(DIZ_CATALOGUE);
    }
}
