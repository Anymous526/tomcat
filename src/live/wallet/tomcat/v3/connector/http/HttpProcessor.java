package live.wallet.tomcat.v3.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import live.wallet.tomcat.v3.ServletProcessor;
import live.wallet.tomcat.v3.StaticResourceProcessor;

public class HttpProcessor {

	private HttpConnector connector;
	private HttpRequest request;
	private HttpResponse response;

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) {

		try {
			SocketInputStream input = new SocketInputStream(
					socket.getInputStream());;
			OutputStream output = socket.getOutputStream();
			request = new HttpRequest(input);
			response = new HttpResponse(output);
			response.setRequest(request);
			response.setHeader("", "");

			parseRequest();
			parseHeader();

			if (request.getRequestURI().startsWith("/servlet/")) {
				new ServletProcessor().process(request, response);
			} else {
				 new StaticResourceProcessor().process(request, response);
			}

		} catch (IOException e) {
			
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}
	}

	private void parseHeader() {

	}

	private void parseRequest() {

	}
}
