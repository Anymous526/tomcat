package live.wallet.tomcat.v01;

import java.io.InputStream;

public class Request {
	
	private InputStream input;
	private String requestURI;

	public Request(InputStream input) {
		this.input = input;
	}

	public void parer() {

		try {
			StringBuffer request = new StringBuffer(2048);
			byte[] buffer = new byte[2048];
			int i = input.read(buffer);
			for (int j = 0; j < i; j++) {
				request.append(buffer[j]);
			}
			System.out.print(request.toString());
			requestURI = parseURI(request.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String parseURI(String string) {
		int index1, index2;
		index1 = string.indexOf(' ');
		if (index1 != -1) {
			index2 = string.indexOf(' ', index1 + 1);
			if (index2 > index1)
				return string.substring(index1 + 1, index2);
		}
		return null;
	}

	public String getRequestURI() {
		return requestURI;
	}

}
