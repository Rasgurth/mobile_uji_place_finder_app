package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/organizationalUnits")
public class OrganizationalUnits {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{unit}")
	public Faculties getFacultyByUnit(@PathParam("unit") String unit) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForUnit(unit);
	}
}
