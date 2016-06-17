package live.wallet.tomcat.v5.valves;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

public class ClientIPLoggerValve implements Valve, Contained {

	private Container container;

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;

	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public void invoke(Request request, Response response, ValveContext valueContext)
			throws IOException, ServletException {
		// Pass this request on to the next valve in our pipeline
		valueContext.invokeNext(request, response);
		System.out.println("Client IP Logger Valve");
		ServletRequest sreq = request.getRequest();
		System.out.println(sreq.getRemoteAddr());
		System.out.println("------------------------------------");

	}

}
