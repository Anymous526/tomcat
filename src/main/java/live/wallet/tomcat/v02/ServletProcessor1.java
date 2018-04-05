package live.wallet.tomcat.v02;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {

	@SuppressWarnings({ "resource", "unchecked" })
	public void process(Request request, Response response) {

		try {
			String uri = request.getUri();
			String servletName = uri.substring(uri.lastIndexOf("/") + 1);
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT + File.pathSeparator + "WEB-INF" + File.separator + "classes");
			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			URLClassLoader loader = new URLClassLoader(urls);
			Class<? extends Servlet> clazz = (Class<? extends Servlet>) loader.loadClass(servletName);
			Servlet servlet = (Servlet) clazz.newInstance();
			servlet.service((ServletRequest) request, (ServletResponse) response);

		} catch (MalformedURLException e) {
			e.printStackTrace();
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

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
