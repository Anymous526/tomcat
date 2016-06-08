package live.wallet.tomcat.v2;

import java.io.IOException;

public class StaticResourcesProcessor {

	public void process(Request request, Response response) {
		try {
			response.sentStaticResources();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}