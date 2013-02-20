package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/offices")
public class Offices {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{office}")
	public Faculties getFacultyByOffice(@PathParam("office") String office) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForOffice(office);
	}
}
