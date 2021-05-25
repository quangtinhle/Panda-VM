package dsubService;

import de.fraunhofer.isst.health.ihe.dsub.service.dsub.DSubService;

public interface DsubNotificationRecipient extends DSubService.NotificationListener {
    void startListenning(String address);
}
