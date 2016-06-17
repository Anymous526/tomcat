package live.wallet.tomcat.v03;

import java.io.IOException;


import live.wallet.tomcat.v03.connector.http.HttpRequest;
import live.wallet.tomcat.v03.connector.http.HttpResponse;

public class StaticResourceProcessor {
	
	public void process(HttpRequest request, HttpResponse response){
		try {
			response.sendStaticResource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
