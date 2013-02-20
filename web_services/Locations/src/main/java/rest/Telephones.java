package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/telephones")
public class Telephones {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{phone}")
	public Faculties getFacultyByPhone(@PathParam("phone") String phone) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForPhone(phone);
	}
}
