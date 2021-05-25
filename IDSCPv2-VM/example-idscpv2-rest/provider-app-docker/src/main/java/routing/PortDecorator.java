package routing;

import de.fraunhofer.isst.health.ihe.dsub.service.dsub.DSUBPortDecorator;
import de.fraunhofer.isst.health.ihe.xds.services.utils.SoapLoggerHandler;
import de.fraunhofer.isst.health.ihe.xds.services.utils.red.SamlAssertionHeaderHandler;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import propertyService.*;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortDecorator implements DSUBPortDecorator {

    private static final String ORG_APACHE_CXF_STAX_FORCE_START_LISTENING_DOCUMENT =
            "org.apache.cxf.stax.force-startListening-document";
    private static final String ORG_APACHE_CXF_STAX_MAX_ATTRIBUTE_SIZE =
            "org.apache.cxf.stax.maxAttributeSize";
    private static final long MAX_ATTRIBUTE_SIZE = 484615165156151L;

    private static final Logger LOGGER = Logger.getLogger(PortDecorator.class.getName());
    private final RoutingService routingService;
    private final PropertyService propertyService;

    public PortDecorator(RoutingService routingService, PropertyService propertyService) {
        this.routingService = routingService;
        this.propertyService = propertyService;
    }

    @Override
    public void decorate(Client client, BindingProvider bindingProvider) {
        Endpoint endpoint = client.getEndpoint();
        bindingProvider.getRequestContext().put(ORG_APACHE_CXF_STAX_FORCE_START_LISTENING_DOCUMENT, Boolean.TRUE);

        // Add SAML Assertion from Shiro to SOAP Header
        List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
        handlerChain.add(new SamlAssertionHeaderHandler());
        handlerChain.add(new SoapLoggerHandler());

        bindingProvider.getBinding().setHandlerChain(handlerChain);

        // Forces Apache CXF to user UTF-8 encodung for incomming messages
        endpoint.getEndpointInfo()
                .getBinding()
                .setProperty(ORG_APACHE_CXF_STAX_MAX_ATTRIBUTE_SIZE, MAX_ATTRIBUTE_SIZE);
        // cxfEndpoint.getOutInterceptors().add(new ClientCertInterceptor(Paths.get(""), ""));

        RoutingInterceptor routingInterceptor = new RoutingInterceptor(routingService);
        endpoint.getOutInterceptors().add(routingInterceptor);
        //cxfEndpoint.getOutInterceptors().add(new AuthorizationHeaderHandler());
        LOGGER.log(Level.INFO, "PortDecorator is using profile: " + propertyService.getSmithProfile());
        if (PropertyServiceImpl.SMITH_PROFILE_PRODUCTION.equals(propertyService.getSmithProfile())) {
            endpoint.getOutInterceptors().add(createAuthInterceptor());
        }
        if (propertyService.isLogRequests()) {
            endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
            endpoint.getInInterceptors().add(new LoggingInInterceptor());
        }
        // cxfEndpoint.getOutInterceptors().add(new TlsInterceptor());

        // Turn off chunking
        HTTPConduit http = (HTTPConduit) client.getConduit();
        http.setClient(new HTTPClientPolicy());
    }

    private Interceptor<? extends Message> createAuthInterceptor() {
        Map<String, Object> outProps = new HashMap<>();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        // Specify our username
        outProps.put(WSHandlerConstants.USER, "_system");
        // Password type : plain text
        outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        // for hashed password use:
        //properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // Callback used to retrieve password for given user.
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, UsernamePasswordCallbackHandler.class.getName());

        return new WSS4JOutInterceptor(outProps);
    }
}
