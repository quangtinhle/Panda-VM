import de.fraunhofer.isst.health.ihe.commons.domain.Code;
import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.dsub.service.mock.DSUBMockService;
import de.fraunhofer.isst.health.ihe.dsub.service.mock.DSUBMockServiceImpl;
import de.fraunhofer.isst.health.ihe.xds.api.RegistryService;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xds.api.domain.author.Author;
import de.fraunhofer.isst.health.ihe.xds.api.domain.status.AvailabilityStatus;
import de.fraunhofer.isst.health.ihe.xds.services.exceptions.MissingAttributeException;
import dsubService.DsubNotificationRecipientImpl;
import httpConnection.HttpConnection;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import registryService.RegistryServiceImpl;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {

    static String recipientEndpoint = "http://localhost:8099/dsub/notification";



    public static void main(String[] args) throws MissingAttributeException {

        DsubNotificationRecipientImpl dsubNotificationRecipient = new DsubNotificationRecipientImpl();
        //DSUBMockService dsubMockService = new DSUBMockServiceImpl();
        dsubNotificationRecipient.startListenning(recipientEndpoint);

        //List<DocumentEntry> list = new ArrayList<>();
        //list.add(getTestDocumentEntry());

        //dsubMockService.sendFullNotification(recipientEndpoint,list);


    }


    //Get Mockupdata from Registry Service
    private static DocumentEntry getTestDocumentEntry() {
        RegistryService registryService = new RegistryServiceImpl();
        return registryService.getDocument("urn:uuid:b592568c-adc0-4e17-b3e5-8914239a438a");
    }



}
