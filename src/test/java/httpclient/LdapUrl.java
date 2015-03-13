package httpclient;

import com.mongodb.BasicDBObject;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by root on 15-3-13.
 */
public class LdapUrl {

    @Test
    public void LdapUrlTest(){
        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost("http://10.58.11.4:9001/atpservice/atp/getAllStateAtp");
//        String name = JOptionPane.showInputDialog("Name atguser");
//        String pwd = JOptionPane.showInputDialog("Pwd atg@2012");
//        UsernamePasswordCredentials upc = new UsernamePasswordCredentials(name, pwd);

        CloseableHttpResponse resp = null;
        //10.58.11.4:9001
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope("10.58.11.4", 9001),
//                new UsernamePasswordCredentials("atguser","atg@2012"));
//
//        HttpClientContext context = HttpClientContext.create();
//        context.setCredentialsProvider(credsProvider);

        post.addHeader("Content-Type","applcation/json");
        post.addHeader("Authorization","YXRndXNlcjphdGdAMjAxMg==");

        BasicDBObject obj = new BasicDBObject().append("itemFlag","N").append("partNum","1000407978");
        System.out.print(obj.toString());
        StringEntity entity = new StringEntity(obj.toString(),"utf-8");

        entity.setContentEncoding("UTF-8");
        entity.setContentType("applcation/json");
        post.setEntity(entity);

        int status = 0;
        try {
            resp = client.execute(post);
            resp.getStatusLine().getStatusCode();
            String bodyStr = EntityUtils.toString(resp.getEntity()); //获取请求结果
            System.out.print(bodyStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(status == 200)
        {
            System.out.println("status code:"+200);
        }
    }

    @Test
    public void httpClient(){

    }
}
