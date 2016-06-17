package live.wallet.tomcat.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "WebContent";
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	private boolean shutdown = false;

	public static void main(String[] args) {
		new HttpServer().await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		Socket socket = null;
		String IP = "127.0.0.1";

		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName(IP));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!shutdown) {
			try {
				InputStream input;
				OutputStream output;
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				Request request = new Request(input);
				request.parer();
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResources();
				socket.close();
				shutdown = request.getRequestURI().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
