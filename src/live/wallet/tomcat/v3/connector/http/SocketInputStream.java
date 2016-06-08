package live.wallet.tomcat.v3.connector.http;

import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {

	private static final byte CR = '\r';
	private static final byte LF = '\n';
	private static final byte SP = ' ';
	private static final byte HT = '\t';
	private static final byte COLON = ':';
	private static final int LC_OFFSET = 'A' - 'a';

	private InputStream is;

	public SocketInputStream(InputStream is) {
		this.is = is;
	}

	public void readRequestLine() {

	}

	public void readHeader() {

	}

	@Override
	public int read() throws IOException {
		return 0;
	}

}
