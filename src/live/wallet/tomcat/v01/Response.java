package live.wallet.tomcat.v01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
	private OutputStream output;
	private Request request;
	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void sendStaticResources() {
		byte[] buffer = new byte[1024];
		FileInputStream fis = null;
		try {
			File file = new File(HttpServer.WEB_ROOT, request.getRequestURI());
			if (file.exists()) {
				fis = new FileInputStream(file);
				int ch = fis.read(buffer, 0, 1024);
				while (ch != -1) {
					output.write(buffer, 0, ch);
					ch = fis.read(buffer, 0, 1024);
				}
			} else {
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
						+ "Content-Type: text/html\r\n"
						+ "Content-Length: 23\r\n" + "\r\n"
						+ "<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
