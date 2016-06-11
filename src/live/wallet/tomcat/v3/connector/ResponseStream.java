package live.wallet.tomcat.v3.connector;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import live.wallet.tomcat.v3.connector.http.HttpResponse;

public class ResponseStream extends ServletOutputStream {
	/**
	 * Has this stream been closed?
	 */
	private boolean closed;

	/**
	 * Should we commit the response when we are flushed?
	 */
	private boolean commit;

	/**
	 * The number of bytes which have already been written to this stream.
	 */
	private int count;

	/**
	 * The content length past which we will not write, or -1 if there is no
	 * defined content length.
	 */
	private int length;

	/**
	 * The Response with which this input stream is associated.
	 */
	private HttpResponse response;

	/**
	 * The underlying output stream to which we should write data.
	 */
	protected OutputStream stream;

	public ResponseStream(HttpResponse response) {
		super();
		closed = false;
		commit = false;
		count = 0;
		this.response = response;
	}

	/**
	 * Write the specified byte to our output stream.
	 *
	 * @param b
	 *            The byte to be written
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	@Override
	public void write(int b) throws IOException {

		if (closed) {
			throw new IOException("responseStream.write.closed");
		}

		if ((length > 0) && (count >= length)) {
			throw new IOException("responseStream.write.count");
		}

		response.write(b);
		count++;
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (closed) {
			throw new IOException("responseStream.write.closed");
		}
		int actual = len;
		if ((length > 0) && ((count + len) >= length))
			actual = length - count;
		response.write(b, off, actual);
		count += actual;
		if (actual < len) {
			throw new IOException("responseStream.write.count");
		}
	}

	/**
	 * Close this output stream, causing any buffered data to be flushed and any
	 * further output data to throw an IOException.
	 */
	public void close() throws IOException {
		if (closed) {
			throw new IOException("responseStream.close.closed");
		}

		response.flushBuffer();
		closed = true;
	}

	/**
	 * Flush any buffered data for this output stream, which also causes the
	 * response to be committed.
	 */
	public void flush() throws IOException {

		if (closed) {
			throw new IOException("responseStream.flush.closed");
		}

		if (commit) {
			response.flushBuffer();
		}

	}

	/**
	 * Reset the count of bytes written to this stream to zero.
	 */
	public void reset() {
		count = 0;
	}

	/**
	 * Has this response stream been closed?
	 */
	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * [Package Private] Return the "commit response on flush" flag.
	 */
	public boolean isCommit() {
		return commit;
	}

	public void setCommit(boolean b) {
		this.commit = b;
	}

}
