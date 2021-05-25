package httpConnection;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpConnection {

    private CloseableHttpClient client;
    private HttpPost httpPost;

    public HttpConnection(String url) {
        client = HttpClients.createDefault();
        httpPost = new HttpPost(url);
        setHeader();
    }

    public HttpPost getHttpPost(){
        return this.httpPost;
    }

    public CloseableHttpClient getClient() {
        return this.client;
    }

    private void setHeader() {
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Url","consumer-core:29292");
        //httpPost.setHeader("Url","panda.isst.fraunhofer.de:29292");
        //httpPost.setHeader("Url","idsconnector-test1.dortmund.isst.fraunhofer.de:29292");
        //httpPost.setHeader("Url","trustedconnector.aisec.fraunhofer.de:9300");
    }


}
