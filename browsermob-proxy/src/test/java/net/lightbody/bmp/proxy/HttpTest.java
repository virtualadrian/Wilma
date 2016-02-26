package net.lightbody.bmp.proxy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class HttpTest extends ProxyServerTest {


    @Test
    public void testBbc() throws Exception {
        get("http://www.bbc.com/");
    }

    @Test
    public void testCnn() throws Exception {
        get("http://cnn.com/");
    }

    @Test
    public void testAccuWeather() throws Exception {
        get("http://www.accuweather.com/");
    }

    /*
    @Test
    public void testNewRelic() throws Exception {
        // see https://github.com/webmetrics/browsermob-proxy/issues/105
        proxy.remapHost("foo.newrelic.com", "rpm.newrelic.com");
        proxy.remapHost("bar.newrelic.com", "rpm.newrelic.com");
        get("https://foo.newrelic.com/");
        get("https://bar.newrelic.com/");
        get("https://rpm.newrelic.com/");
    }
    */

    private void get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = client.execute(get);
        EntityUtils.consumeQuietly(response.getEntity());
        Assert.assertEquals("Expected 200 when fetching " + url, 200, response.getStatusLine().getStatusCode());
    }
}
