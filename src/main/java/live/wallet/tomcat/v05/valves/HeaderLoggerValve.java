package live.wallet.tomcat.v05.valves;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

public class HeaderLoggerValve implements Valve, Contained {
	private Container container;

	@Override
	public Container getContainer() {
		return this.container;
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
	public void invoke(Request request, Response response, ValveContext valueContext) throws IOException, ServletException {
		// Pass this request on to the next valve in our pipeline
		valueContext.invokeNext(request, response);

		System.out.println("Header Logger Valve");
		ServletRequest sreq = request.getRequest();
		if (sreq instanceof HttpServletRequest) {
			HttpServletRequest hreq = (HttpServletRequest) sreq;
			Enumeration<?> headerNames = hreq.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement().toString();
				String headerValue = hreq.getHeader(headerName);
				System.out.println(headerName + ":" + headerValue);
			}

		} else
			System.out.println("Not an HTTP Request");

		System.out.println("------------------------------------");

	}

}
