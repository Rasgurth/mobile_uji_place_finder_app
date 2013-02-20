package rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class MyApp extends Application {
	@Override
	 public Set<Class<?>> getClasses() {
	  Set<Class<?>> s = new HashSet<Class<?>>();
	        s.add(Mails.class);
	        s.add(Offices.class);
	        s.add(OrganizationalUnits.class);
	        s.add(People.class);
	        s.add(Telephones.class);
	        s.add(Titles.class);
	        return s;
	 }
}
