package httpclient;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class HttpClientTest {


    @Test
    public String getContentByUrl(String url,int timeOut){
        String bodyStr = "";
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse resp = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig reqConf = RequestConfig.custom()  //设置请求超时时间
                    .setConnectionRequestTimeout(timeOut)
                    .setConnectTimeout(timeOut)
                    .setSocketTimeout(timeOut)
                    .build();
            httpGet.setConfig(reqConf);
            resp = client.execute(httpGet);
            if(resp.getStatusLine().getStatusCode()==200){//获取相应码
                bodyStr = EntityUtils.toString(resp.getEntity()); //获取请求结果
            }
        }catch(SocketTimeoutException e){
            System.out.println("Socket超时！");
        }catch(ConnectTimeoutException e){
            System.out.println("Connect超时！");
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(resp!=null){
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bodyStr;
    }
}
