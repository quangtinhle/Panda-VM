package providerService;

import de.fraunhofer.isst.health.ihe.commons.domain.ID;
import de.fraunhofer.isst.health.ihe.xds.api.RegistryService;
import de.fraunhofer.isst.health.ihe.xds.api.RepositoryService;
import de.fraunhofer.isst.health.ihe.xds.api.domain.DocumentEntry;
import de.fraunhofer.isst.health.ihe.xdw.services.ValidationException;
import de.fraunhofer.isst.health.ihe.xdw.services.XdwDocument;
import de.fraunhofer.isst.health.ihe.xdw.services.task.XdwTask;
import httpConnection.HttpConnection;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.eclipse.jetty.util.IO;
import org.hl7.v3.SouthernAlaska;
import registryService.RegistryServiceImpl;
import repositoryService.RepositoryServiceImpl;

import javax.json.Json;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProviderServiceImpl {


    private final RepositoryService repositoryService;
    private final RegistryService registryService;
    static final String repoID = "1.3.6.1.4.1.21367.2011.2.3.323";

    private String url = "http://localhost:8880/data";
    //private String url = "http://153.96.23.42:8880/data";

    public ProviderServiceImpl () {
        repositoryService = new RepositoryServiceImpl();
        registryService = new RegistryServiceImpl();
    }


   public void processResponseDocument(String entryUUID) throws ValidationException, IOException {
       DocumentEntry documentwithEUUID = getRegistryDocumentwithEUUID(entryUUID);
       XdwDocument xdwDocument = getXdwDocumentfromInputStream(getInputStreamDocumentContent(documentwithEUUID));
       if(xdwDocument.getWorkflowStatus().getValue() =="OPEN" ) {
           List<XdwTask> taskList = xdwDocument.getTaskList();
           for (XdwTask xdwTask: taskList
                ) {
               String uniqueID = xdwTask.getInput().get(0).getIdentifier().split("/")[0];
               InputStream inputStream = repositoryService.getDocumentContent(uniqueID, repoID);
               String string = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
               sendDatatoTrustedConnector(string);
           }
       }

   }

    private void sendDatatoTrustedConnector(String data) throws IOException {
        HttpConnection connection = new HttpConnection(url);
        StringEntity entity = new StringEntity(data);
        HttpPost httpPost = connection.getHttpPost();
        CloseableHttpClient client = connection.getClient();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(httpPost);
        client.close();
    }


   private InputStream getInputStreamDocumentContent(DocumentEntry documentEntry) {
       InputStream inputStream = null;
        try {
           inputStream = repositoryService.getDocumentContent(documentEntry.getUniqueId(), documentEntry.getRepositoryUniqueId());
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
        return inputStream;
   }
    private DocumentEntry getRegistryDocumentwithEUUID(String entryUUID) {
        return registryService.getDocument(entryUUID);
    }

    private XdwDocument getXdwDocumentfromInputStream(InputStream inputStream) throws IOException, ValidationException {
        String documentContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        return XdwDocument.fromXml(documentContent,StandardCharsets.UTF_8);
    }
    //Load Resource File on Local
    private String loadResourceFile(String filePath) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
        List<String> fileLines = Files.readAllLines(path);
        StringBuilder result = new StringBuilder();
        fileLines.forEach(line -> result.append(line).append("\n"));
        return result.toString();
    }
    //Get Xdw from Resource
    private XdwDocument getXdwFilefromResource() throws IOException, URISyntaxException, ValidationException {
        //String storedXml = loadResourceFile("MockupXdw.xml");
        String storedJson = loadResourceFile("SMITH_Ausschnitt-Testdaten_DUP_2021-04-29.json");
        XdwDocument xdwDocument = XdwDocument.fromXml(storedJson,StandardCharsets.UTF_8);
        return xdwDocument;
    }

}
