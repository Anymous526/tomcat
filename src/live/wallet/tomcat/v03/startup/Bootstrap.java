package live.wallet.tomcat.v3.startup;

import live.wallet.tomcat.v3.connector.http.HttpConnector;

public class Bootstrap {
	public static void main(String[] args) {
		HttpConnector connectot = new HttpConnector();
		connectot.start();
	}
}
