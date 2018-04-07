package live.wallet.tomcat.dn.tomcat2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static int count = 0;

	public static void main(String[] args) {
		ServerSocket ss = null;
		Socket socket = null;
		try {
			ss = new ServerSocket(9999);
			System.out.println("服务器已经初始化");

			while (true) {
				socket = ss.accept();

				HttpRequest request = new HttpRequest(socket.getInputStream());
				HttpResponse response = new HttpResponse(socket.getOutputStream());
				String uri = request.getUri();

				if (isStatic(uri)) {
					response.writerFile(uri.substring(1));
				}

				String html = "<html><head><title>SSB</title></head><body>当前时间: "
						+ "<br/> 服务器回复 : <font size='12' color='blue'> 哈哈哈 </font></body></html>";

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isStatic(String uri) {
		boolean isStatic = false;
		String[] suffixs = { "html", "css", "jpg", "js" };
		for (String suffix : suffixs) {
			if (uri.endsWith("." + suffix)) {
				isStatic = true;
				break;
			}
		}
		return isStatic;
	}
}
