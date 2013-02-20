package ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import model.Faculties;
import model.Faculty;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class LdapSearch {
	private final static String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private final static String PROVIDER_URL = "ldap://ldap.uji.es/";
	private final static String SECURITY_AUTHENTICATION = "none";
	private final static String SUBDIRECTORY = "o=Universitat Jaume I,c=ES";
	private final static String FILTER_INIT = "(&(objectclass=person)(roomNumber=";
	private final static String FILTER_END = "*))";
	private final static String FILTER_INIT_FOR_NAME = "(&(objectclass=person)(name=";
	private final static String FILTER_INIT_FOR_MAIL = "(&(objectclass=person)(mail=*";
	private final static String FILTER_INIT_FOR_UNIT = "(&(objectclass=person)(ou=";
	private final static String FILTER_INIT_FOR_PHONE = "(&(objectclass=person)(telephonenumber=*";
	private final static String FILTER_INIT_FOR_TITLE = "(&(objectclass=person)(title=";
	private final static String URL_BASE_PERSONAL_WEB = "http://www3.uji.es/~";
	private Hashtable<String, String> connectionProperties;
	private LdapContext context;
	private Faculties faculties;
	private Faculty faculty;

	public LdapSearch() {
		super();
		defineConnectionParameters();
		createContext();
	}

	private final void defineConnectionParameters() {
		connectionProperties = new Hashtable<String, String>();
		connectionProperties.put(Context.INITIAL_CONTEXT_FACTORY,
				INITIAL_CONTEXT_FACTORY);
		connectionProperties.put(Context.PROVIDER_URL, PROVIDER_URL);
		connectionProperties.put(Context.SECURITY_AUTHENTICATION,
				SECURITY_AUTHENTICATION);
	}

	private final void createContext() {
		try {
			context = new InitialLdapContext(connectionProperties, null);
			context.setRequestControls(null);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Faculties searchLdapDirectoryForOffice(String office) {
		faculties = new Faculties();
		String filter = FILTER_INIT + office + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}
	
	public Faculties searchLdapDirectoryForName(String name) {
		faculties = new Faculties();
		String filter = FILTER_INIT_FOR_NAME + name + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}
	
//
	public Faculties searchLdapDirectoryForMail(String mail) {
		faculties = new Faculties();
		String filter = FILTER_INIT_FOR_MAIL + mail + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}
	
	public Faculties searchLdapDirectoryForUnit(String unit) {
		faculties = new Faculties();
		String filter = FILTER_INIT_FOR_UNIT + unit + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}
	
	public Faculties searchLdapDirectoryForPhone(String phone) {
		faculties = new Faculties();
		String filter = FILTER_INIT_FOR_PHONE + phone + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}
	
	public Faculties searchLdapDirectoryForTitle(String title) {
		faculties = new Faculties();
		String filter = FILTER_INIT_FOR_TITLE + title + FILTER_END;
		searchForFaculties(filter);
		return faculties;
	}

	private void searchForFaculties(String filter) {
		try {
			NamingEnumeration<?> results = context.search(SUBDIRECTORY, filter,
					getSimpleSarchControls());
			processSearchResult(results);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private SearchControls getSimpleSarchControls() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		return searchControls;
	}

	private void processSearchResult(NamingEnumeration<?> results)
			throws NamingException {
		SearchResult result;
		Attributes attributes;
		faculty = null;
		while (results.hasMore()) {
			result = (SearchResult) results.next();
			attributes = result.getAttributes();
			setSuitableFaculty(attributes);
			populateFaculty(attributes);
		}
	}

	private void setSuitableFaculty(Attributes attributes) {
		if (faculty == null) {
			createAndAddNewFaculty();
		} else {
			getSuitableFaculty(attributes);
		}
	}
	
	private void createAndAddNewFaculty() {
		faculty = new Faculty();
		faculties.addFaculty(faculty);
	}
	
	private void getSuitableFaculty(Attributes attributes) {
		Faculty existingFaculty = faculties
				.getFacultyByCommonName(getAttributes(attributes, "cn"));
		if (existingFaculty != Faculty.NULL) {
			faculty = existingFaculty;
		} else {
			faculties.addFaculty(faculty);
			faculty = new Faculty();
		}
	}
	
	private void populateFaculty(Attributes attributes) {
		faculty.addCommonName(getAttributes(attributes, "cn"));
		faculty.addDescription(getAttributes(attributes, "description"));
		faculty.addMail(getAttributes(attributes, "mail"));
		faculty.addRoomNumber(getAttributes(attributes, "roomNumber"));
		faculty.addTelephoneNumber(getAttributes(attributes, "telephoneNumber"));
		faculty.addUid(getAttributes(attributes, "uid"));
		faculty.addOrganizationalUnit(getAttributes(attributes, "ou"));
		if (faculty.getUids().size() > 0)
			faculty.addPersonalWebAddress(getPersonalWebAddress(attributes));
	}

	private List<String> getPersonalWebAddress(Attributes attributes) {
		String webAddress;
		List<String> webAddresses = new ArrayList<String>();
		List<String> uids = getAttributes(attributes, "uid");
		try {
			for (String uid : uids) {
				webAddress = URL_BASE_PERSONAL_WEB + uid;
				if (isWebAddressOK(webAddress))
					webAddresses.add(webAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return webAddresses;
	}

	private List<String> getAttributes(Attributes attributes,
			String attributeCode) {
		List<String> result = new ArrayList<String>();
		Attribute attribute = attributes.get(attributeCode);
		if (attribute != null)
			try {
				for (int i = 0; i < attribute.size(); i++)
					result.add(attribute.get(i).toString());
			} catch (NamingException e) {
				e.printStackTrace();
			}
		
		return result;
	}
	
	private boolean isWebAddressOK(String webAddress) throws Exception {
		StatusLine statusLine = getStatusLineForGettingWebAddress(webAddress);
		if (statusLine.getStatusCode() == HttpStatus.SC_OK)
			return true;
		else
			return false;
	}

	private StatusLine getStatusLineForGettingWebAddress(String webAddress)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(webAddress);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		return httpResponse.getStatusLine();

	}

}
