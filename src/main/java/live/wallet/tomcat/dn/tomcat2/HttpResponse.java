package live.wallet.tomcat.dn.tomcat2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
	private OutputStream os = null;

	public HttpResponse(OutputStream os) throws IOException {
		this.os = os;
	}

	/**
	 * 响应静态文件
	 */
	public void writerFile(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = fis.read(buff)) != -1) {
			os.write(buff, 0, len);
		}
		fis.close();
		os.flush();
		os.close();
	}

}
