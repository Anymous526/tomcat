package live.wallet.tomcat.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer1 {

	public static void main(String[] args) {
		HttpServer1 server = new HttpServer1();
		server.await();
	}

	private void await() {
		ServerSocket serversocket = null;
		Socket socket = null;
		int port = 8080;
		String ip = "127.0.0.1";

		try {
			serversocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (true) {
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serversocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				Request request = new Request(input);
				request.parse();
				
				Response response = new Response(output);
				response.setRequest(request);
				
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor1 processor = new ServletProcessor1();
					processor.process(request, response);
				} else {
					StaticResourcesProcessor processor = new StaticResourcesProcessor();
					processor.process(request, response);
				}

				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket != null) {
						socket.close();
						socket = null;
					}
					if (serversocket != null) {
						serversocket.close();
						serversocket = null;
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}

			}

		}
	}
}
