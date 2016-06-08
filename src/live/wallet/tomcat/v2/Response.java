package live.wallet.tomcat.v2;

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

	public void sentStaticResources() {
		// TODO Auto-generated method stub
		
	}

}
