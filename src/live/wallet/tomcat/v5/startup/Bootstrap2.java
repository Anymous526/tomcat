package live.wallet.tomcat.v5.startup;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

import live.wallet.tomcat.v5.core.SimpleContext;
import live.wallet.tomcat.v5.core.SimpleContextMapper;
import live.wallet.tomcat.v5.core.SimpleLoader;
import live.wallet.tomcat.v5.core.SimpleWrapper;
import live.wallet.tomcat.v5.valves.ClientIPLoggerValve;
import live.wallet.tomcat.v5.valves.HeaderLoggerValve;


@SuppressWarnings("deprecation")
public class Bootstrap2 {

	public static void main(String[] args) {

		HttpConnector connector = new HttpConnector();
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");
		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");

		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);

		Valve valve1 = new HeaderLoggerValve();
		Valve valve2 = new ClientIPLoggerValve();

		((Pipeline) context).addValve(valve1);
		((Pipeline) context).addValve(valve2);

		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");
		context.addMapper(mapper);
		Loader loader = new SimpleLoader();
		context.setLoader(loader);
		// context.addServletMapping(pattern, name);
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");
		connector.setContainer(context);
		try {
			connector.initialize();
			connector.start();

			// make the application wait until we press a key.
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
