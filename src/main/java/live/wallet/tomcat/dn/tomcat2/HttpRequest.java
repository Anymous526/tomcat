package live.wallet.tomcat.dn.tomcat2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpRequest {
	private String uri;
	private Map<String, String> parmsMap = new HashMap<String, String>();
	private final static Log LOG = LogFactory.getLog(HttpRequest.class);

	public HttpRequest(InputStream is) throws IOException {
		byte[] buff = new byte[1024];
		int len = is.read(buff);
		if (len > 0) {
			String msg = new String(buff, 0, len);
			// uri = msg.substring(msg.indexOf("/"), msg.indexOf("HTTP/1.1") - 1);
			int beginIndex = msg.indexOf("GET") + 4;
			if (msg.indexOf("POST") != -1) {
				beginIndex = msg.indexOf("POST") + 5;
			}
			int endIndex = msg.indexOf("HTTP/1.1") - 1;
			uri = msg.substring(beginIndex, endIndex);
			parseRequestParam(msg);
		} else {
			System.out.println("bad request !");
		}

	}

	private void parseRequestParam(String msg) {
		// 解析参数
		String parmString = null;
		if (msg.startsWith("GET")) {
			LOG.info("get 请求 URI:" + uri);
		} else if (msg.startsWith("POST")) {
			int paramStart = msg.lastIndexOf("\n");
			parmString = msg.substring(paramStart + 1);
			LOG.info("POST方式请深圳市的表单参数:" + parmString);
			if (parmString != null && ("".equals(parmString))) {
				if (parmString.contains("&")) {
					String[] parms = parmString.split("&");
					for (String parm : parms) {
						String[] parmTemp = parm.split("=");
						parmsMap.put(parmTemp[0], parmTemp[1]);
						LOG.info(parmTemp[0] + " : " + parmTemp[1]);
					}
				} else {
					if (parmString.contains("=")) {
						String[] parmTemp = parmString.split("=");
						parmsMap.put(parmTemp[0], parmTemp[1]);
						LOG.info(parmTemp[0] + " : " + parmTemp[1]);
					}
				}
			}
		}
	}

	public String getParamter(String key) {
		return parmsMap.get(key);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
