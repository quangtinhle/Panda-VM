package routing;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoutingInterceptor extends AbstractPhaseInterceptor<Message> {

    private final Logger logger;
    private Map<String, String> routingMap = new HashMap<>();
    private RoutingService routingService;

    public RoutingInterceptor(RoutingService routingService) {
        super(Phase.POST_LOGICAL);
        this.logger = Logger.getLogger(this.getClass().getName());
        this.routingService = routingService;
        fillRoutingMap();
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (message.get(JAXWSAConstants.ADDRESSING_PROPERTIES_OUTBOUND) instanceof AddressingProperties) {
            AddressingProperties addressingProperties = (AddressingProperties)
                    message.get(JAXWSAConstants.ADDRESSING_PROPERTIES_OUTBOUND);

            // Extract SOAP-Action from message
            String soapAction = "";
            if (addressingProperties.getAction() != null) {
                soapAction = addressingProperties.getAction().getValue();
            }

            // Change the target endpoint depending on SOAP-Action
            if (addressingProperties.getAction() != null && StringUtils.isNotEmpty(soapAction)) {
                message.put(Message.ENDPOINT_ADDRESS, routingMap.get(soapAction));
                addressingProperties.getTo().setValue(routingMap.get(soapAction));
            }
        }
    }

    private void fillRoutingMap() {
        String registryUrl = routingService.getRegistryEndpoint();
        String repositoryUrl = routingService.getRepositoryEndpoint();
        logger.log(Level.INFO, "Using registry url: " + registryUrl);
        logger.log(Level.INFO, "Using repository url: " + repositoryUrl);
        routingMap.put(ITI.ITI_18_REGISTRY_STORED_QUERY, registryUrl);
        routingMap.put(ITI.ITI_41_PROVIDE_AND_REGISTER_DOCUMENT_SET_B, repositoryUrl);
        routingMap.put(ITI.ITI_43_RETRIEVE_DOCUMENT_SET, repositoryUrl);
        routingMap.put(ITI.ITI_42_REGISTER_DOCUMENT_SET_B, registryUrl);
    }

    public static final class ITI {
        private ITI() {
        }

        static final String ITI_18_REGISTRY_STORED_QUERY = "urn:ihe:iti:2007:RegistryStoredQuery";
        static final String ITI_18_QUERY_GET_FOLDER_AND_CONTENTS = "urn:uuid:b909a503-523d-4517-8acf-8e5834dfc4c7";
        static final String ITI_18_QUERY_FIND_FOLDERS = "urn:uuid:958f3006-baad-4929-a4de-ff1114824431";
        static final String ITI_41_PROVIDE_AND_REGISTER_DOCUMENT_SET_B = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
        static final String ITI_43_RETRIEVE_DOCUMENT_SET = "urn:ihe:iti:2007:RetrieveDocumentSet";
        static final String ITI_44_PIXV3_PATIENT_FEED = "urn:hl7-org:v3:PRPA_IN201301UV02";
        static final String ITI_45_PIXV3_QUERY = "urn:hl7-org:v3:PRPA_IN201309UV02";
        static final String ITI_47_PDQ3_QUERY = "urn:ihe:iti:2010:ProviderInformationQueryRequest";
        static final String ITI_57_UPDATE_DOCUMENT_SET = "urn:ihe:iti:2010:UpdateDocumentSet";
        static final String ITI_58_HPD_QUERY = "urn:ihe:iti:2010:ProviderInformationQueryRequest";
        static final String ITI_42_REGISTER_DOCUMENT_SET_B = "urn:ihe:iti:2007:RegisterDocumentSet-b";
    }

}
