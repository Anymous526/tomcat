package live.wallet.tomcat.v3.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.catalina.util.StringManager;

public class SocketInputStream extends InputStream {

	public static final byte CR = '\r';
	public static final byte LF = '\n';
	public static final byte SP = ' ';
	public static final byte HT = '\t';
	public static final byte COLON = ':';
	public static final int LC_OFFSET = 'A' - 'a';
	/**
	 * Internal buffer.
	 */
	protected byte buf[];

	/**
	 * Last valid byte.
	 */
	protected int count;

	/**
	 * Position in the buffer.
	 */
	protected int pos;

	protected static StringManager sm = StringManager.getManager(Constants.PACKAGE);

	private InputStream is;

	public SocketInputStream(InputStream is, int bufferSize) {
		this.is = is;
		buf = new byte[bufferSize];
	}

	public void readRequestLine(HttpRequestLine requestLine) throws IOException {

		// Recycling check
		if (requestLine.methodEnd != 0)
			requestLine.recycle();

		// Checking for a blank line
		int chr = 0;
		do { // Skipping CR or LF
			try {
				chr = read();
			} catch (IOException e) {
				chr = -1;
			}
		} while ((chr == CR) || (chr == LF));
		if (chr == -1)
			throw new EOFException(sm.getString("requestStream.readline.error"));
		pos--;

		// Reading the method name

		int maxRead = requestLine.method.length;
		int readStart = pos;
		int readCount = 0;

		boolean space = false;

		while (!space) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.method, 0, newBuffer, 0, maxRead);
					requestLine.method = newBuffer;
					maxRead = requestLine.method.length;
				} else {
					throw new IOException(sm.getString("requestStream.readline.toolong"));
				}
			}
			// We're at the end of the internal buffer
			if (pos >= count) {
				int val = read();
				if (val == -1) {
					throw new IOException(sm.getString("requestStream.readline.error"));
				}
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == SP) {
				space = true;
			}
			requestLine.method[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}

		requestLine.methodEnd = readCount - 1;

		// Reading URI

		maxRead = requestLine.uri.length;
		readStart = pos;
		readCount = 0;

		space = false;

		boolean eol = false;

		while (!space) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_URI_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.uri, 0, newBuffer, 0, maxRead);
					requestLine.uri = newBuffer;
					maxRead = requestLine.uri.length;
				} else {
					throw new IOException(sm.getString("requestStream.readline.toolong"));
				}
			}
			// We're at the end of the internal buffer
			if (pos >= count) {
				int val = read();
				if (val == -1)
					throw new IOException(sm.getString("requestStream.readline.error"));
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == SP) {
				space = true;
			} else if ((buf[pos] == CR) || (buf[pos] == LF)) {
				// HTTP/0.9 style request
				eol = true;
				space = true;
			}
			requestLine.uri[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}

		requestLine.uriEnd = readCount - 1;

		// Reading protocol

		maxRead = requestLine.protocol.length;
		readStart = pos;
		readCount = 0;

		while (!eol) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.protocol, 0, newBuffer, 0, maxRead);
					requestLine.protocol = newBuffer;
					maxRead = requestLine.protocol.length;
				} else {
					throw new IOException(sm.getString("requestStream.readline.toolong"));
				}
			}
			// We're at the end of the internal buffer
			if (pos >= count) {
				// Copying part (or all) of the internal buffer to the line
				// buffer
				int val = read();
				if (val == -1)
					throw new IOException(sm.getString("requestStream.readline.error"));
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == CR) {
				// Skip CR.
			} else if (buf[pos] == LF) {
				eol = true;
			} else {
				requestLine.protocol[readCount] = (char) buf[pos];
				readCount++;
			}
			pos++;
		}

		requestLine.protocolEnd = readCount;

	}

	public void readHeader() {

	}

	@Override
	public int read() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}
		return buf[pos++] & 0xff;
	}

	private void fill() throws IOException {
		pos = 0;
		count = 0;
		int nRead = is.read(buf, 0, buf.length);
		if (nRead > 0) {
			count = nRead;
		}

	}

	public void readHeader(HttpHeader header) {

	}

}
