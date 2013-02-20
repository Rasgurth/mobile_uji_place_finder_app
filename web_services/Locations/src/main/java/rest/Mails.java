package rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ldap.LdapSearch;
import model.Faculties;

@Path("/mails")
public class Mails {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{mail}")
	public Faculties getFacultyByMail(@PathParam("mail") String mail) {
		LdapSearch ldap = new LdapSearch();
		return ldap.searchLdapDirectoryForMail(mail);
	}
}
