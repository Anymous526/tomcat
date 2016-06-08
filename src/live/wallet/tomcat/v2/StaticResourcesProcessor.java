package live.wallet.tomcat.v2;

public class StaticResourcesProcessor {

	public void process(Request request, Response response) {
		response.sentStaticResources();
	}
}
