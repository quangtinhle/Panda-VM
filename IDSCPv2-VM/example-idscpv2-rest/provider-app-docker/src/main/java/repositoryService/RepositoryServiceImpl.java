package repositoryService;

import de.fraunhofer.isst.health.ihe.atna.servicelogger.IHEEventLogger;
import de.fraunhofer.isst.health.ihe.xds.api.RepositoryService;
import de.fraunhofer.isst.health.ihe.xds.services.IHERepositoryService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import propertyService.PropertyService;
import propertyService.PropertyServiceImpl;
import routing.PortDecorator;
import routing.PortalRoutingServiceImpl;

import javax.inject.Inject;
import javax.xml.ws.BindingProvider;

public class RepositoryServiceImpl extends IHERepositoryService implements RepositoryService {


    //private String repositoryEndpoint = "http://153.96.23.42:90/ws/repository";
    @Override
    protected String getRepositoryURL() {
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
        System.out.println(portalRoutingService.getRepositoryEndpoint());
        return new PortDecorator(portalRoutingService, propertyService);
    }

    @Override
    protected IHEEventLogger useIheEventLogger() {
        return null;
    }
}
