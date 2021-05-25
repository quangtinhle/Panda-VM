package routing;

import propertyService.*;

import javax.inject.Inject;

public class PortalRoutingServiceImpl implements RoutingService {

    private PropertyService propertyService;

    @Inject
    public PortalRoutingServiceImpl(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public String getRegistryEndpoint() {
        return propertyService.getIHERegistryUrl();
    }

    @Override
    public String getRepositoryEndpoint() {
        return propertyService.getIHERepositoryUrl();
    }
}
