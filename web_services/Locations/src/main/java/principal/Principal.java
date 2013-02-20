package principal;

import ldap.LdapSearch;

public class Principal {
	public static void main(String[] args) {
		LdapSearch ldap = new LdapSearch();
		System.out.println(ldap.searchLdapDirectoryForOffice("TI1208"));
	}
}
