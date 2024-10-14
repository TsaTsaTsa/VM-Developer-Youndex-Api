package ru.hse.tsantsaridi;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class YandexCloudAPI {
    private String apiToken;

    public YandexCloudAPI(String apiToken) {
        this.apiToken = apiToken;
    }

    public String createVM(String name, String diskType, String diskSize, String imageId) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://cloud-api.yandex.net/v1/compute/instances");

        String json = "{" +
                "\"name\":\"" + name + "\"," +
                "\"resourcesSpec\":{" +
                "\"diskType\":\"" + diskType + "\"," +
                "\"diskSize\":\"" + diskSize + "\"," +
                "\"imageId\":\"" + imageId + "\"" +
                "}" +
                "}";

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Authorization", "Bearer " + apiToken);
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        client.close();
        return responseString;
    }
}
