package live.wallet.tomcat.v7.core;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.InstanceListener;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.LifecycleSupport;

import ex07.pyrmont.core.SimplePipeline;
import ex07.pyrmont.core.SimpleWrapperValve;

public class SimpleWrapper implements Wrapper, Pipeline, Lifecycle {
	// the servlet instance
	private Servlet instance = null;
	private String servletClass;
	private Loader loader;
	private String name;
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	private SimplePipeline pipeline = new SimplePipeline(this);
	protected Container parent = null;
	protected boolean started = false;

	public SimpleWrapper() {
		pipeline.setBasic(new SimpleWrapperValve());
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Loader getLoader() {
		if (loader != null)
			return (loader);
		if (parent != null)
			return (parent.getLoader());
		return (null);
	}

	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;

	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public Manager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setManager(Manager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public Cluster getCluster() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCluster(Cluster cluster) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public void setParent(Container container) {
		this.parent = container;
	}

	@Override
	public ClassLoader getParentClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public Realm getRealm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRealm(Realm realm) {
		// TODO Auto-generated method stub

	}

	@Override
	public DirContext getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResources(DirContext resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChild(Container child) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addContainerListener(ContainerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMapper(Mapper mapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Container findChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container[] findChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapper findMapper(String protocol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		pipeline.invoke(request, response);

	}

	@Override
	public Container map(Request request, boolean update) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChild(Container child) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMapper(Mapper mapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws LifecycleException {
		System.out.println("Starting Wrapper " + name);
		if (started)
			throw new LifecycleException("Wrapper already started");

		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
		started = true;

		// Start our subordinate components, if any
		if ((loader != null) && (loader instanceof Lifecycle))
			((Lifecycle) loader).start();

		// Start the Valves in our pipeline (including the basic), if any
		if (pipeline instanceof Lifecycle)
			((Lifecycle) pipeline).start();

		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);

	}

	@Override
	public void stop() throws LifecycleException {
		System.out.println("Stopping wrapper " + name);
		// Shut down our servlet instance (if it has been initialized)
		try {
			instance.destroy();
		} catch (Throwable t) {
		}
		instance = null;
		if (!started)
			throw new LifecycleException("Wrapper " + name + " not started");
		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);

		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;

		// Stop the Valves in our pipeline (including the basic), if any
		if (pipeline instanceof Lifecycle) {
			((Lifecycle) pipeline).stop();
		}

		// Stop our subordinate components, if any
		if ((loader != null) && (loader instanceof Lifecycle)) {
			((Lifecycle) loader).stop();
		}

		// Notify our interested LifecycleListeners
		lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);

	}

	@Override
	public Valve getBasic() {
		return pipeline.getBasic();
	}

	@Override
	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);

	}

	@Override
	public synchronized void addValve(Valve valve) {
		pipeline.addValve(valve);

	}

	@Override
	public Valve[] getValves() {
		return pipeline.getValves();
	}

	@Override
	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);

	}

	@Override
	public long getAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAvailable(long available) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getJspFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJspFile(String jspFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoadOnStartup() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLoadOnStartup(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRunAs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRunAs(String runAs) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServletClass() {
		return this.servletClass;
	}

	@Override
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;

	}

	@Override
	public boolean isUnavailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addInitParameter(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstanceListener(InstanceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSecurityReference(String name, String link) {
		// TODO Auto-generated method stub

	}

	@Override
	public Servlet allocate() throws ServletException {
		// Load and initialize our instance if necessary
		if (instance == null) {
			try {
				instance = loadServlet();
			} catch (ServletException e) {
				throw e;
			} catch (Throwable e) {
				throw new ServletException("Cannot allocate a servlet instance", e);
			}
		}
		return instance;
	}

	public Servlet loadServlet() throws ServletException {
		if (instance != null)
			return instance;

		Servlet servlet = null;
		String actualClass = servletClass;
		if (actualClass == null) {
			throw new ServletException("servlet class has not been specified");
		}

		Loader loader = getLoader();
		// Acquire an instance of the class loader to be used
		if (loader == null) {
			throw new ServletException("No loader.");
		}
		ClassLoader classLoader = loader.getClassLoader();

		// Load the specified servlet class from the appropriate class loader
		Class<?> clazz = null;
		try {
			if (classLoader != null) {
				clazz = classLoader.loadClass(actualClass);
			}
		} catch (ClassNotFoundException e) {
			throw new ServletException("Servlet class not found");
		}
		// Instantiate and initialize an instance of the servlet class itself
		try {
			servlet = (Servlet) clazz.newInstance();
		} catch (Throwable e) {
			throw new ServletException("Failed to instantiate servlet");
		}

		// Call the initialization method of this servlet
		try {
			servlet.init(null);
		} catch (Throwable f) {
			throw new ServletException("Failed initialize servlet.");
		}
		return servlet;
	}

	@Override
	public void deallocate(Servlet servlet) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public String findInitParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findInitParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findSecurityReference(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findSecurityReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInitParameter(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInstanceListener(InstanceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSecurityReference(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unavailable(UnavailableException unavailable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() throws ServletException {
		// TODO Auto-generated method stub

	}
}
