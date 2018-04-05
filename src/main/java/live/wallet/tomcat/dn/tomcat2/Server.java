package live.wallet.tomcat.dn.tomcat2;

import java.io.IOException;
import java.io.InputStream;
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

				OutputStream os = socket.getOutputStream();
				String html = "<html><head><title>SSB</title></head><body>当前时间: "
						+ "<br/> 服务器回复 : <font size='12' color='blue'> 哈哈哈 </font></body></html>";
				os.write(html.getBytes("GBK"));
				os.flush();
				os.close();
				socket.close();
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
}
