package live.wallet.tomcat.v3.connector.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.util.CookieTools;
import org.apache.tomcat.util.http.Cookies;

import live.wallet.tomcat.v3.connector.ResponseStream;
import live.wallet.tomcat.v3.connector.ResponseWriter;

public class HttpResponse implements HttpServletResponse {

	private static final int BUFFER_SIZE = 1024;

	private OutputStream output;
	private HttpRequest request;
	private PrintWriter writer;
	private byte[] buffer;
	private int bufferCount;
	/**
	 * Has this response been committed yet?
	 */
	private boolean committed;
	/**
	 * The actual number of bytes written to this Response.
	 */
	private int contentCount;
	/**
	 * The content length associated with this Response.
	 */
	private int contentLength;

	/**
	 * The content type associated with this Response.
	 */
	private String contentType;
	/**
	 * The character encoding associated with this Response.
	 */
	private String encoding;

	/**
	 * The set of Cookies associated with this Response.
	 */
	protected ArrayList<Cookie> cookies = new ArrayList<Cookie>();
	/**
	 * The HTTP headers explicitly added via addHeader(), but not including
	 * those to be added with setContentLength(), setContentType(), and so on.
	 * This collection is keyed by the header name, and the elements are
	 * ArrayLists containing the associated values that have been set.
	 */
	protected HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
	/**
	 * The date format we will use for creating date headers.
	 */
	private final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	/**
	 * The error message set by <code>sendError()</code>.
	 */
	private String message;
	/**
	 * The HTTP status code associated with this Response.
	 */
	private int status;

	public HttpResponse(OutputStream output) {
		this.output = output;
	}

	public void sendHeads() {

		if (isCommitted())
			return;
		// Prepare a suitable output writer
		OutputStreamWriter osr = null;
		try {
			osr = new OutputStreamWriter(getStream(), getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			osr = new OutputStreamWriter(getStream());
		}
		final PrintWriter outputWriter = new PrintWriter(osr);
		// Send the "Status:" header
		outputWriter.print(this.getProtocol());
		outputWriter.print(" ");
		outputWriter.print(status);
		if (message != null) {
			outputWriter.print(" ");
			outputWriter.print(message);
		}
		outputWriter.print("\r\n");
		// Send the content-length and content-type headers (if any)
		if (getContentType() != null) {
			outputWriter.print("Content-Type: " + getContentType() + "\r\n");
		}
		if (getContentLength() >= 0) {
			outputWriter.print("Content-Length: " + getContentLength() + "\r\n");
		}
		// Send all specified headers (if any)
		synchronized (headers) {
			Iterator<String> names = headers.keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				ArrayList<String> values = headers.get(name);
				Iterator<String> items = values.iterator();
				while (items.hasNext()) {
					String value = (String) items.next();
					outputWriter.print(name);
					outputWriter.print(": ");
					outputWriter.print(value);
					outputWriter.print("\r\n");
				}
			}
		}
		// Add the session ID cookie if necessary
		/*
		 * HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
		 * HttpSession session = hreq.getSession(false); if ((session != null)
		 * && session.isNew() && (getContext() != null) &&
		 * getContext().getCookies()) { Cookie cookie = new Cookie("JSESSIONID",
		 * session.getId()); cookie.setMaxAge(-1); String contextPath = null; if
		 * (context != null) contextPath = context.getPath(); if ((contextPath
		 * != null) && (contextPath.length() > 0)) cookie.setPath(contextPath);
		 * else
		 * 
		 * cookie.setPath("/"); if (hreq.isSecure()) cookie.setSecure(true);
		 * addCookie(cookie); }
		 */
		// Send all specified cookies (if any)
		synchronized (cookies) {
			Iterator<Cookie> items = cookies.iterator();
			while (items.hasNext()) {
				Cookie cookie = items.next();
				outputWriter.print(CookieTools.getCookieHeaderName(cookie));
				outputWriter.print(": ");
				outputWriter.print(CookieTools.getCookieHeaderValue(cookie));
				outputWriter.print("\r\n");
			}
		}

		// Send a terminating blank line to mark the end of the headers
		outputWriter.print("\r\n");
		outputWriter.flush();

		committed = true;

	}

	private int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Object getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	private char[] getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	private OutputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a default status message for the specified HTTP status code.
	 *
	 * @param status
	 *            The status code for which a message is desired
	 */
	private String getStatusMessage(int status) {
		switch (status) {
		case SC_OK:
			return ("OK");
		case SC_ACCEPTED:
			return ("Accepted");
		case SC_BAD_GATEWAY:
			return ("Bad Gateway");
		case SC_BAD_REQUEST:
			return ("Bad Request");
		case SC_CONFLICT:
			return ("Conflict");
		case SC_CONTINUE:
			return ("Continue");
		case SC_CREATED:
			return ("Created");
		case SC_EXPECTATION_FAILED:
			return ("Expectation Failed");
		case SC_FORBIDDEN:
			return ("Forbidden");
		case SC_GATEWAY_TIMEOUT:
			return ("Gateway Timeout");
		case SC_GONE:
			return ("Gone");
		case SC_HTTP_VERSION_NOT_SUPPORTED:
			return ("HTTP Version Not Supported");
		case SC_INTERNAL_SERVER_ERROR:
			return ("Internal Server Error");
		case SC_LENGTH_REQUIRED:
			return ("Length Required");
		case SC_METHOD_NOT_ALLOWED:
			return ("Method Not Allowed");
		case SC_MOVED_PERMANENTLY:
			return ("Moved Permanently");
		case SC_MOVED_TEMPORARILY:
			return ("Moved Temporarily");
		case SC_MULTIPLE_CHOICES:
			return ("Multiple Choices");
		case SC_NO_CONTENT:
			return ("No Content");
		case SC_NON_AUTHORITATIVE_INFORMATION:
			return ("Non-Authoritative Information");
		case SC_NOT_ACCEPTABLE:
			return ("Not Acceptable");
		case SC_NOT_FOUND:
			return ("Not Found");
		case SC_NOT_IMPLEMENTED:
			return ("Not Implemented");
		case SC_NOT_MODIFIED:
			return ("Not Modified");
		case SC_PARTIAL_CONTENT:
			return ("Partial Content");
		case SC_PAYMENT_REQUIRED:
			return ("Payment Required");
		case SC_PRECONDITION_FAILED:
			return ("Precondition Failed");
		case SC_PROXY_AUTHENTICATION_REQUIRED:
			return ("Proxy Authentication Required");
		case SC_REQUEST_ENTITY_TOO_LARGE:
			return ("Request Entity Too Large");
		case SC_REQUEST_TIMEOUT:
			return ("Request Timeout");
		case SC_REQUEST_URI_TOO_LONG:
			return ("Request URI Too Long");
		case SC_REQUESTED_RANGE_NOT_SATISFIABLE:
			return ("Requested Range Not Satisfiable");
		case SC_RESET_CONTENT:
			return ("Reset Content");
		case SC_SEE_OTHER:
			return ("See Other");
		case SC_SERVICE_UNAVAILABLE:
			return ("Service Unavailable");
		case SC_SWITCHING_PROTOCOLS:
			return ("Switching Protocols");
		case SC_UNAUTHORIZED:
			return ("Unauthorized");
		case SC_UNSUPPORTED_MEDIA_TYPE:
			return ("Unsupported Media Type");
		case SC_USE_PROXY:
			return ("Use Proxy");
		case 207: // WebDAV
			return ("Multi-Status");
		case 422: // WebDAV
			return ("Unprocessable Entity");
		case 423: // WebDAV
			return ("Locked");
		case 507: // WebDAV
			return ("Insufficient Storage");
		default:
			return ("HTTP Response Status " + status);
		}
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setHeader(String name, String value) {
		// if (included)
		// return;
		// Ignore any call from an included servlet
		if (!isCommitted()) {
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			synchronized (headers) {
				headers.put(name, values);
			}
			String match = name.toLowerCase();
			if (match.equals("content-length")) {
				int contentLength = -1;
				try {
					contentLength = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (contentLength >= 0)
					setContentLength(contentLength);
			} else if (match.equals("content-type")) {
				setContentType(value);
			}
		}

	}

	public void finishResponse() {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			/* request.getUri has been replaced by request.getRequestURI */
			File file = new File(Constants.WEB_ROOT, request.getRequestURI());
			fis = new FileInputStream(file);
			/*
			 * HTTP Response = Status-Line (( general-header | response-header |
			 * entity-header ) CRLF) CRLF [ message-body ] Status-Line =
			 * HTTP-Version SP Status-Code SP Reason-Phrase CRLF
			 */
			int ch = fis.read(bytes, 0, BUFFER_SIZE);
			while (ch != -1) {
				output.write(bytes, 0, ch);
				ch = fis.read(bytes, 0, BUFFER_SIZE);
			}
		} catch (FileNotFoundException e) {
			String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
					+ "Content-Length: 23\r\n" + "\r\n" + "<h1>File Not Found</h1>";
			output.write(errorMessage.getBytes());
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	@Override
	public String getCharacterEncoding() {
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}

		return encoding;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		ResponseStream newStream = new ResponseStream(this);
		newStream.setCommit(false);
		OutputStreamWriter osr = new OutputStreamWriter(newStream, getCharacterEncoding());
		writer = new ResponseWriter(osr);
		return writer;
	}

	@Override
	public void setContentLength(int len) {
		this.contentLength = len;
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public void setBufferSize(int size) {

	}

	@Override
	public int getBufferSize() {

		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsHeader(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc, String sm) {
		// TODO Auto-generated method stub

	}

	public void write(int b) {
		// TODO Auto-generated method stub

	}

	public void write(byte[] b, int off, int actual) {
		// TODO Auto-generated method stub

	}

}
