package live.wallet.tomcat.v03.startup;

import live.wallet.tomcat.v03.connector.http.HttpConnector;

public class Bootstrap {
	public static void main(String[] args) {
		HttpConnector connectot = new HttpConnector();
		connectot.start();
	}
}
