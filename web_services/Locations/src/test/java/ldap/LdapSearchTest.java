package ldap;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.*;

import model.Faculties;
import model.Faculty;

import org.junit.Test;

public class LdapSearchTest {

	@Test
	public void recuperaUbicacion() {
		LdapSearch ldap = new LdapSearch();
		Faculties faculties = ldap.searchLdapDirectoryForOffice("TI1203");
		assertNotNull(faculties);
		assertEquals(1, faculties.getFaculties().size());
		assertNotNull(faculties.getFaculties().get(0).getCommonNames());
		assertThat(faculties.getFaculties().get(0).getCommonNames().get(0), containsString("Oscar"));
	}

}
