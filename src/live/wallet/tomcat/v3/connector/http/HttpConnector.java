package live.wallet.tomcat.v3.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import live.wallet.tomcat.v3.connector.http.HttpProcessor;

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

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public String getScheme() {
		return scheme;
	}
}
