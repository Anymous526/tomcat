package live.wallet.tomcat.dn.tomcat2;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpRequest {
	private String uri;
	private final static Log LOG = LogFactory.getLog(HttpRequest.class);

	public HttpRequest(InputStream is) throws IOException {
		byte[] buff = new byte[1024];
		int len = is.read(buff);
		if (len > 0) {
			String msg = new String(buff, 0, len);
			uri = msg.substring(msg.indexOf("/"), msg.indexOf("HTTP/1.1") - 1);
			LOG.info("uri: " + uri);
		} else {
			System.out.println("bad request !");
		}
	}
}
