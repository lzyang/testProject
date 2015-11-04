package jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Morningsun(515190653@qq.com) on 15-11-2.
 */
public class JsoupCore {

    @Test
    public void simple(){
        Connection conn = Jsoup.connect("http://eye.ds.gome.com.cn");
        try {
            conn.timeout(1500);
            Document doc = conn.get();
            Element head = doc.head();
            Elements title = head.getElementsByTag("title");
            System.out.println(title.html());
//            System.out.println(doc.outerHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void response(){
        Connection conn = Jsoup.connect("http://eye.ds.gome.com.cn");
        try {
            conn.timeout(1500);
            Connection.Response resp = conn.execute();

            System.out.println("contentType:" + resp.contentType());
            System.out.println("charset:" + resp.charset());
            System.out.println("statusMessage:" + resp.statusMessage());
            System.out.println("statusCode:" + resp.statusCode());
            System.out.println("headers[]:" + resp.headers());
            System.out.println("method:" + resp.method());
            System.out.println("url:" + resp.url());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
