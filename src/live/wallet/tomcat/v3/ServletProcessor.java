package live.wallet.tomcat.v3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import live.wallet.tomcat.v3.connector.http.Constants;
import live.wallet.tomcat.v3.connector.http.HttpRequest;
import live.wallet.tomcat.v3.connector.http.HttpRequestFacade;
import live.wallet.tomcat.v3.connector.http.HttpResponse;
import live.wallet.tomcat.v3.connector.http.HttpResponseFacade;

public class ServletProcessor {

	public void process(HttpRequest request, HttpResponse response) {

		try {
			String uri = request.getRequestURI();
			String servletName = uri.substring(uri.lastIndexOf("/") + 1);
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT + File.separator + "WEB-INF" + File.separator + "classes");
			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			URLClassLoader loader = new URLClassLoader(urls);
			Class<? extends Servlet> clazz = (Class<? extends Servlet>) loader.loadClass(servletName);
			Servlet servlet = clazz.newInstance();
			HttpRequestFacade requestFacade = new HttpRequestFacade(request);
			HttpResponseFacade responseFacade = new HttpResponseFacade(response);
			servlet.service(requestFacade, responseFacade);
			response.finishResponse();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}
}
