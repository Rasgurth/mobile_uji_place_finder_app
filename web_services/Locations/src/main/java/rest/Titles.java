package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/titles")
public class Titles {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{title}")
	public Faculties getFacultyByTitle(@PathParam("title") String title) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForTitle(title);
	}
}
