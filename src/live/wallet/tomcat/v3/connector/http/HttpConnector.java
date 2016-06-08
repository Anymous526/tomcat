package live.wallet.tomcat.v3.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import live.wallet.tomcat.v3.connector.http.HttpProcessor;

public class HttpConnector implements Runnable {

	private boolean stopped = false;
	
	
	public void run() {
		ServerSocket ss = null;
		int port = 8080;
		try {
			ss = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!stopped) {
			Socket socket = null;
			try {
				socket = ss.accept();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}

	public void start() {
		new Thread(this).start();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

}
