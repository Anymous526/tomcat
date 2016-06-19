package live.wallet.tomcat.v07.core;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class SimpleContextLifecycleListener implements LifecycleListener {

	@SuppressWarnings("unused")
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		Lifecycle lifecycle = event.getLifecycle();
		System.out.println("SimpleContextLifecycleListener's event " + event.getType().toString());
		if (Lifecycle.START_EVENT.equals(event.getType())) {
			System.out.println("Starting context.");
		} else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
			System.out.println("Starting context.");
		}

	}

}
