package httpclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JavaHttpClient45Demo implements Runnable{
    // 代理服务器
    final static String proxyHost = "http-dyn.abuyun.com";
    final static Integer proxyPort = 9020;

    // 代理隧道验证信息
    final static String proxyUser = "HJ55562X2H63649D";
    final static String proxyPass = "F54D15B0EA4F2760";

    private static PoolingHttpClientConnectionManager cm = null;
    private static HttpRequestRetryHandler httpRequestRetryHandler = null;
    private static HttpHost proxy = null;

    private static CredentialsProvider credsProvider = null;
    private static RequestConfig reqConfig = null;

    public static long gtotalCount = 0;
    public static long gnow = System.currentTimeMillis();
    public static long gdealTime = 0;
    public static long gsCount = 0;
    public static long eCount = 0;

    public static long totalCount = 0;

    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry registry = RegistryBuilder.create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();

        cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(5);

        proxy = new HttpHost(proxyHost, proxyPort, "http");

        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

        reqConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setExpectContinueEnabled(false)
                .setProxy(new HttpHost(proxyHost, proxyPort))
                .build();
    }

    public void doRequest(HttpRequestBase httpReq) {
        CloseableHttpResponse httpResp = null;

        try {
            setHeaders(httpReq);

            httpReq.setConfig(reqConfig);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();

            AuthCache authCache = new BasicAuthCache();
            authCache.put(proxy, new BasicScheme());

            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            httpResp = httpClient.execute(httpReq, localContext);

            int statusCode = httpResp.getStatusLine().getStatusCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent()));

            String line = "";
            String msg = "";
            boolean flag = false;
            while((line = rd.readLine()) != null) {
//                System.out.println(line);
                msg = msg + line;
                if(line.contains("J_5105026")){
                    flag = true;
                }
            }
            System.out.println(msg);
            //parseDom(msg);
//            synchronized (proxyPort) {
//                gdealTime = System.currentTimeMillis() - gnow;
//                gtotalCount ++;
//                if (!flag) {
//                    eCount++;
//                } else {
//                    gsCount++;
////                    System.out.println(msg);
//                }
//                if (gdealTime > 100000) {
//                    System.out.println(Thread.currentThread().getName() + " > time window:" + gdealTime + ". Avg " + gdealTime / gsCount * 1.0 + " times per req. count:" + gsCount + ". errCount:" + eCount + " err:" + eCount*100.0/gtotalCount + "%");
//                    gnow = System.currentTimeMillis();
//                    gsCount = 0;
//                    eCount = 0;
//                    gtotalCount = 0;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpResp != null) {
                    httpResp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置请求头
     *
     * @param httpReq
     */
    private static void setHeaders(HttpRequestBase httpReq) {
        httpReq.setHeader("Accept-Encoding", null);
    }

    public void doPostRequest() {
        try {
            // 要访问的目标页面
            HttpPost httpPost = new HttpPost("https://test.abuyun.com/proxy.php");

            // 设置表单参数
            List params = new ArrayList();
            params.add(new BasicNameValuePair("method", "next"));
            params.add(new BasicNameValuePair("params", "{\"broker\":\"abuyun\":\"site\":\"https://www.abuyun.com\"}"));

            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            doRequest(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doGetRequest() {
        // 要访问的目标页面
        String targetUrl = "http://p.3.cn/prices/get?skuid=J_5105026";
        //String targetUrl = "http://proxy.abuyun.com/switch-ip";
        //String targetUrl = "http://proxy.abuyun.com/current-ip";
//        String targetUrl = "http://www.whatismyip.com.tw";
//        String targetUrl = "http://ip.chinaz.com/";

        try {
            HttpGet httpGet = new HttpGet(targetUrl);
            doRequest(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseDom(String html){
        Document doc = Jsoup.parse(html);
//        Element element = doc.getElementById("ip-json");
        Elements elements = doc.getElementsByClass("fz24");
        Element ipElement = elements.first();
        Element areaElement = ipElement.nextElementSibling().nextElementSibling();
        System.out.println(++totalCount + "   " + ipElement.html() + "   " + areaElement.html().replace("\n<a href=\"http://tool.chinaz.com/contact\" target=\"_blank\" class=\"col-blue02 pl5\">(纠错)</a>",""));
        return elements.html();
    }

    @Override
    public void run() {
        long totalCount = 0;
        long now = System.currentTimeMillis();
        long dealTime = 0;
        long sCount = 0;
        while (true) {
            doGetRequest();
            totalCount++;
            sCount++;
            if (totalCount == 1) {
                break;
            }
//            dealTime = System.currentTimeMillis() - now;
//            if (dealTime > 1000) {
//                System.out.println(Thread.currentThread().getName() + " > time window:" + dealTime + ". Avg " + dealTime / sCount * 1.0 + " times per req. TotalCount:" + sCount);
//                now = System.currentTimeMillis();
//                sCount = 0;
//            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new JavaHttpClient45Demo());
        t1.setName("t1");
        t1.start();

//        Thread t2 = new Thread(new JavaHttpClient45Demo());
//        t2.setName("t2");
//        t2.start();
//
//        Thread t3 = new Thread(new JavaHttpClient45Demo());
//        t3.setName("t3");
//        t3.start();
//
//        Thread t4 = new Thread(new JavaHttpClient45Demo());
//        t4.setName("t4");
//        t4.start();
    }
    //doPostRequest();
}
