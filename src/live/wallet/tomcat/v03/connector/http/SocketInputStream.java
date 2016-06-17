package live.wallet.tomcat.v03.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.catalina.util.StringManager;

@SuppressWarnings("unused")
public class SocketInputStream extends InputStream {

	public static final byte CR = (byte) '\r';
	public static final byte LF = (byte) '\n';
	public static final byte SP = (byte) ' ';
	public static final byte HT = (byte) '\t';
	public static final byte COLON = (byte) ':';
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

	private InputStream is;

	protected static StringManager sm = StringManager.getManager(Constants.PACKAGE);

	public SocketInputStream(InputStream is, int bufferSize) {
		this.is = is;
		buf = new byte[bufferSize];
	}

	/**
	 * Read the request line, and copies it to the given buffer. This function
	 * is meant to be used during the HTTP request header parsing. Do NOT
	 * attempt to read the request body using it.
	 *
	 * @param requestLine
	 *            Request line object
	 * @throws IOException
	 *             If an exception occurs during the underlying socket read
	 *             operations, or if the given buffer is not big enough to
	 *             accomodate the whole line.
	 */
	public void readRequestLine(HttpRequestLine requestLine) throws IOException {

		// Recycling check
		if (requestLine.getMethodEnd() != 0)
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

		int maxRead = requestLine.getMethod().length;

		int readStart = pos;
		int readCount = 0;

		boolean space = false;

		while (!space) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.getMethod(), 0, newBuffer, 0, maxRead);
					requestLine.setMethod(newBuffer);
					maxRead = requestLine.getMethod().length;
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
			requestLine.getMethod()[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}

		requestLine.setMethodEnd(readCount - 1);

		// Reading URI

		maxRead = requestLine.getUri().length;
		readStart = pos;
		readCount = 0;

		space = false;

		boolean eol = false;

		while (!space) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_URI_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.getUri(), 0, newBuffer, 0, maxRead);
					requestLine.setUri(newBuffer);
					maxRead = requestLine.getUri().length;
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
			requestLine.getUri()[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}

		requestLine.setUriEnd(readCount - 1);

		// Reading protocol

		maxRead = requestLine.getProtocol().length;
		readStart = pos;
		readCount = 0;

		while (!eol) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.getProtocol(), 0, newBuffer, 0, maxRead);
					requestLine.setProtocol(newBuffer);
					maxRead = requestLine.getProtocol().length;
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
				requestLine.getProtocol()[readCount] = (char) buf[pos];
				readCount++;
			}
			pos++;
		}

		requestLine.setProtocolEnd(readCount);

	}

	/**
	 * Read a header, and copies it to the given buffer. This function is meant
	 * to be used during the HTTP request header parsing. Do NOT attempt to read
	 * the request body using it.
	 *
	 * @param requestLine
	 *            Request line object
	 * @throws IOException
	 *             If an exception occurs during the underlying socket read
	 *             operations, or if the given buffer is not big enough to
	 *             accomodate the whole line.
	 */
	public void readHeader(HttpHeader header) throws IOException {

		// Recycling check
		if (header.getNameEnd() != 0)
			header.recycle();

		// Checking for a blank line
		int chr = read();
		if ((chr == CR) || (chr == LF)) { // Skipping CR
			if (chr == CR)
				read(); // Skipping LF
			header.setNameEnd(0);
			header.setValueEnd(0);
			return;
		} else {
			pos--;
		}

		// Reading the header name

		int maxRead = header.getName().length;
		int readStart = pos;
		int readCount = 0;

		boolean colon = false;

		while (!colon) {
			// if the buffer is full, extend it
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpHeader.MAX_NAME_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(header.getName(), 0, newBuffer, 0, maxRead);
					header.setName(newBuffer);
					maxRead = header.getName().length;
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
			if (buf[pos] == COLON) {
				colon = true;
			}
			char val = (char) buf[pos];
			if ((val >= 'A') && (val <= 'Z')) {
				val = (char) (val - LC_OFFSET);
			}
			header.getName()[readCount] = val;
			readCount++;
			pos++;
		}

		header.setNameEnd(readCount - 1);

		// Reading the header value (which can be spanned over multiple lines)

		maxRead = header.getValue().length;
		readStart = pos;
		readCount = 0;

		int crPos = -2;

		boolean eol = false;
		boolean validLine = true;

		while (validLine) {

			boolean space = true;

			// Skipping spaces
			// Note : Only leading white spaces are removed. Trailing white
			// spaces are not.
			while (space) {
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
				if ((buf[pos] == SP) || (buf[pos] == HT)) {
					pos++;
				} else {
					space = false;
				}
			}

			while (!eol) {
				// if the buffer is full, extend it
				if (readCount >= maxRead) {
					if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
						char[] newBuffer = new char[2 * maxRead];
						System.arraycopy(header.getValue(), 0, newBuffer, 0, maxRead);
						header.setValue(newBuffer);
						maxRead = header.getValue().length;
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
				} else if (buf[pos] == LF) {
					eol = true;
				} else {
					// FIXME : Check if binary conversion is working fine
					int ch = buf[pos] & 0xff;
					header.getValue()[readCount] = (char) ch;
					readCount++;
				}
				pos++;
			}

			int nextChr = read();

			if ((nextChr != SP) && (nextChr != HT)) {
				pos--;
				validLine = false;
			} else {
				eol = false;
				// if the buffer is full, extend it
				if (readCount >= maxRead) {
					if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
						char[] newBuffer = new char[2 * maxRead];
						System.arraycopy(header.getValue(), 0, newBuffer, 0, maxRead);
						header.setValue(newBuffer);
						maxRead = header.getValue().length;
					} else {
						throw new IOException(sm.getString("requestStream.readline.toolong"));
					}
				}
				header.getValue()[readCount] = ' ';
				readCount++;
			}

		}

		header.setValueEnd(readCount);

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

	/**
	 * Returns the number of bytes that can be read from this input stream
	 * without blocking.
	 */
	public int available() throws IOException {
		return (count - pos) + is.available();
	}

	/**
	 * Close the input stream.
	 */
	public void close() throws IOException {
		if (is == null)
			return;
		is.close();
		is = null;
		buf = null;
	}

	/**
	 * Fill the internal buffer using data from the undelying input stream.
	 */
	protected void fill() throws IOException {
		pos = 0;
		count = 0;
		int nRead = is.read(buf, 0, buf.length);
		if (nRead > 0) {
			count = nRead;
		}
	}

}
