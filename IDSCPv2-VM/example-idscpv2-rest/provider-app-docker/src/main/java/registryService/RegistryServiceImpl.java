package registryService;

import de.fraunhofer.isst.health.ihe.atna.servicelogger.IHEEventLogger;
import de.fraunhofer.isst.health.ihe.xds.api.RegistryService;
import de.fraunhofer.isst.health.ihe.xds.services.IHERegistryService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import propertyService.PropertyService;
import propertyService.PropertyServiceImpl;
import routing.PortDecorator;
import routing.PortalRoutingServiceImpl;

import javax.inject.Inject;
import javax.xml.ws.BindingProvider;

public class RegistryServiceImpl extends IHERegistryService implements RegistryService {

    //private String registryEndpoint = "http://localhost:90/ws/registry";
    //private String registryEndpoint = "http://153.96.23.42:90/ws/registry";
    @Override
    protected String getRegistryURL() {
        return null;
    }

    @Override
    protected String getUpdateURL() {
        return null;
    }

    @Override
    protected <T> T port(T t) {
        Client client = ClientProxy.getClient(t);
        BindingProvider bindingProvider = (BindingProvider) t;
        getPortDecorator().decorate(client, bindingProvider);
        return t;
    }
    private PortDecorator getPortDecorator() {
        PropertyService propertyService = new PropertyServiceImpl();
        PortalRoutingServiceImpl portalRoutingService = new PortalRoutingServiceImpl(propertyService);
        return new PortDecorator(portalRoutingService, propertyService);
    }
    @Override
    protected IHEEventLogger useIheEventLogger() {
        return null;
    }
}
