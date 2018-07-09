package com.sonar.util;

import com.sonar.constant.WebApi;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.HashMap;
import java.util.Map;

public class ProjectUtil {

    private static final Logger logger = Logger.getLogger(ProjectUtil.class);

    public static final String webHost = "http://jabdl3504.it:9113/";
    public static final String webUser = "admin";
    public static final String webPassword = "admin";

    public static void main(String[] args) {

        String projectId = "ce4c03d6-430f-40a9-b777-ad877c00aa4d";

        Map<String, Object> params = newParamMap();
        params.put("id", projectId);

        String url = getApi("", params);
        logger.info("url:" + url);


        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
//        HttpGet request = new HttpGet(webHost + "/api/resources?resource=" + projectId + "&depth=-1&scopes=FIL&format=json");
//        request.setHeader("Accept", "application/json");
//        String json = connector.executeRequest(request);
        HttpPost request = new HttpPost(url);
        String json = connector.executeRequest(request);

        logger.info(json);
        logger.info("end......");

    }

    private static Map<String, Object> newParamMap() {
        return new HashMap<>();
    }


    /**
     * @param api
     * @return
     */
    private static String getApi(String api, Map<String, Object> params) {
        StringBuilder apiBuilder = new StringBuilder().append(webHost).append(api);
        if (MapUtils.isEmpty(params)) {
            return apiBuilder.toString();
        }
        apiBuilder.append("?");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            apiBuilder.append(entry.getKey());
            apiBuilder.append("=");
            apiBuilder.append(entry.getValue());
            apiBuilder.append("&");
        }
        String apiUrl = apiBuilder.toString();
        return StringUtils.substring(apiUrl, 0, apiUrl.length() - 1);
    }


}
