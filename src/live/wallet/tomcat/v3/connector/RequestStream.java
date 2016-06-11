package live.wallet.tomcat.v3.connector;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

import org.apache.catalina.util.StringManager;

import live.wallet.tomcat.v3.connector.http.Constants;
import live.wallet.tomcat.v3.connector.http.HttpRequest;

public class RequestStream extends ServletInputStream {

	@SuppressWarnings("unused")
	private HttpRequest request;
	/**
	 * Has this stream been closed?
	 */
	private boolean closed;

	/**
	 * The number of bytes which have already been returned by this stream.
	 */
	private int count;

	/**
	 * The content length past which we will not read, or -1 if there is no
	 * defined content length.
	 */
	private int length;
	/**
	 * The underlying input stream from which we should read data.
	 */
	private InputStream stream;

	private StringManager sm = StringManager.getManager(Constants.PACKAGE);

	/**
	 * Construct a servlet input stream associated with the specified Request.
	 * 
	 * @param request
	 */
	public RequestStream(HttpRequest request) {
		super();
		closed = false;
		count = 0;
		length = request.getContentLength();
		stream = request.getStream();
	}

	@Override
	public int read() throws IOException {
		// Has this stream been closed?
		if (closed) {
			throw new IOException(sm.getString("requestStream.read.closed"));
		}

		// Have we read the specified content length already?
		if ((length >= 0) && (count >= length)) {
			return (-1); // End of file indicator
		}
		// Read and count the next byte, then return it
		int b = stream.read();
		if (b >= 0) {
			count++;
		}
		
		return (b);
	}

	public void close() throws IOException {

		if (closed) {
			throw new IOException(sm.getString("requestStream.close.closed"));
		}

		if (length > 0) {
			while (count < length) {
				int b = read();
				if (b < 0)
					break;
			}
		}

		closed = true;
	}

}
