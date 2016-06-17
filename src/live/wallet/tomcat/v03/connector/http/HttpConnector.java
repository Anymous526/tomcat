package live.wallet.tomcat.v03.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {

	private boolean stopped = false;
	private String scheme = "http";

	public void run() {

		ServerSocket serverSocket = null;
		int port = 8080;
		String ip = "127.0.0.1";

		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!stopped) {
			// Accept the next incoming connection from the server socket
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				continue;
			}

			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public String getScheme() {
		return scheme;
	}
}
