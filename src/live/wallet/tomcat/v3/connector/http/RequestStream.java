package live.wallet.tomcat.v3.connector.http;

import java.io.IOException;
import java.io.InputStream;

public class RequestStream extends InputStream {

	public RequestStream(HttpRequest request) {

	}

	@Override
	public int read() throws IOException {

		return 0;
	}

}
