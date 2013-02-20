package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/people")
public class People {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{name}")
	public Faculties getFacultyByName(@PathParam("name") String name) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForName(name);
	}
}
