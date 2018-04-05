package live.wallet.tomcat.dn.tomcat2;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
	private OutputStream os = null;

	public HttpResponse(OutputStream os) throws IOException {
		this.os = os;
	}

}
