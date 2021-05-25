package dsubService;

import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.dsub.domain.FolderNotification;
import de.fraunhofer.isst.health.ihe.dsub.domain.FullNotification;
import de.fraunhofer.isst.health.ihe.dsub.domain.MinimalNotification;
import de.fraunhofer.isst.health.ihe.dsub.service.dsub.DSubService;
import de.fraunhofer.isst.health.ihe.dsub.service.dsub.DSubServiceImpl;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xdw.services.ValidationException;
import providerService.ProviderServiceImpl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DsubNotificationRecipientImpl implements DsubNotificationRecipient{

    private ProviderServiceImpl providerService;
    private final DSubService dSubService;

    private final Logger logger = Logger.getLogger(DsubNotificationRecipientImpl.class.getName());

    public DsubNotificationRecipientImpl() {
        dSubService = new DSubServiceImpl();
        providerService = new ProviderServiceImpl();
    }


    @Override
    public void onMinimalNotificationReceived(MinimalNotification minimalNotification) {

    }

    @Override
    public void onFullNotificationReceived(FullNotification fullNotification) {
        logger.log(Level.CONFIG,"Received full notification");
        for(DocumentEntry documentEntry : fullNotification.getEntryList()) {
            logger.log(Level.CONFIG,"Received full notification with document title: " + documentEntry.getTitle());
            try {
                providerService.processResponseDocument(documentEntry.getEntryUUID());
            } catch (ValidationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onFolderNotificationReceived(FolderNotification folderNotification) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void startListenning(String address) {
        dSubService.listenOnAddress(address,this);
    }
}
