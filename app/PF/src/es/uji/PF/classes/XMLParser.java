package es.uji.PF.classes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class XMLParser {
	private final static String ns = null;
	
	public XMLParser() {
		super();
	}
	
	public List<Person> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = XmlPullParserFactory.
								   newInstance().
								   newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readPersons(parser);
		} finally {
			in.close();
		}
	}

	private List<Person> readPersons(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Person> persons = new ArrayList<Person>();
		parser.require(XmlPullParser.START_TAG, ns, "faculties");
		System.out.println("Leído faculties(varias)");
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("faculty")){
				System.out.println("Add person");
				persons.add(readPerson(parser));
				System.out.println("Added person");
			}else
				skip(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "faculties");
		System.out.println("Leído end tag de faculties(varias)");
		return persons;
	}

	private Person readPerson(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "faculty");
		System.out.println("Leído Person");
		String commonName = null;
		String description = null;
		String mail = null;
		String roomNumber = null;
		String telephoneNumber = null;
		String organizationalUnit = null;
		String personalWebAddress = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			System.out.println("commonName: " + commonName);
			if(name.equals("commonNames"))
				commonName = readCommonNames(parser);
			else if(name.equals("descriptions"))
				description = readDescriptions(parser);
			else if(name.equals("mails"))
				mail = readMails(parser);
			else if(name.equals("roomNumbers"))
				roomNumber = readRoomNumbers(parser);
			else if(name.equals("telephoneNumbers"))
				telephoneNumber = telephoneNumbers(parser);
			else if(name.equals("organizationalUnits"))
				organizationalUnit = organizationalUnits(parser);
			else if(name.equals("personalWebAddresses"))
				personalWebAddress = readPersonalWebAddress(parser);
			else skip(parser);
		}
		System.out.println(parser.getName());
		parser.require(XmlPullParser.END_TAG, ns, "faculty");
		System.out.println("Leído end tag de Person");
		return new Person(commonName, description, mail, roomNumber, 
							telephoneNumber, organizationalUnit, 
							personalWebAddress);
	}
	
	private String readCommonNames(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "commonNames");
		System.out.println("Leído start tag de commonNames");
		String commonName = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("commonName"))
				if(commonName == null)
					commonName = readFirstCommonName(parser);
				else readFirstCommonName(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "commonNames");
		System.out.println("Leído end tag de commonNames");
		return commonName;
	}

	private String readFirstCommonName(XmlPullParser parser) throws XmlPullParserException, IOException {
		String firstCommonName = null;
		parser.require(XmlPullParser.START_TAG, ns, "commonName");
		System.out.println("Leído start tag commonName");
		firstCommonName = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "commonName");
		System.out.println("Leído end tag commonName");
		return firstCommonName;
	}

	private String readDescriptions(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "descriptions");
		System.out.println("Leído start tag descriptions");
		String description = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("description"))
				if(description == null)
					description = readFirstDescription(parser);
				else readFirstDescription(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "descriptions");
		System.out.println("Leído end tag descriptions");
		return description;
	}
	
	private String readFirstDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
		String description = null;
		parser.require(XmlPullParser.START_TAG, ns, "description");
		System.out.println("Leído start tag description");
		description = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		System.out.println("Leído end tag description");
		return description;
	}

	private String readMails(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "mails");
		System.out.println("Leído start tag mails");
		String mail = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("mail"))
				if(mail == null)
					mail = readFirstMail(parser);
				else readFirstMail(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "mails");
		System.out.println("Leído end tag mails");
		return mail;
	}
	
	private String readFirstMail(XmlPullParser parser) throws XmlPullParserException, IOException {
		String firstMail = null;
		parser.require(XmlPullParser.START_TAG, ns, "mail");
		System.out.println("Leído star tag mail");
		firstMail = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "mail");
		return firstMail;
	}

	private String readRoomNumbers(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "roomNumbers");
		System.out.println("Leído start tag roomNumbers");
		String roomNumber = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("roomNumber"))
				if(roomNumber == null)
					roomNumber = readFirstRoomNumber(parser);
				else readFirstRoomNumber(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "roomNumbers");
		System.out.println("Leído end tag roomNumbers");
		return roomNumber;
	}
	
	private String readFirstRoomNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
		String roomNumber = null;
		parser.require(XmlPullParser.START_TAG, ns, "roomNumber");
		System.out.println("Leído star tag roomNumber");
		roomNumber = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "roomNumber");
		return roomNumber;
	}

	private String telephoneNumbers(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "telephoneNumbers");
		System.out.println("Leído start tag telephoneNumbers");
		String telephoneNumber = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("telephoneNumber"))
				if(telephoneNumber == null)
					telephoneNumber = readFirstTelephoneNumber(parser);
				else readFirstTelephoneNumber(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "telephoneNumbers");
		System.out.println("Leído end tag telephoneNumbers");
		return telephoneNumber;
	}
	
	private String readFirstTelephoneNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
		String firstTelephoneNumber = null;
		parser.require(XmlPullParser.START_TAG, ns, "telephoneNumber");
		System.out.println("Leído start tag telephoneNumber");
		firstTelephoneNumber = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "telephoneNumber");
		System.out.println("Leído end tag telephoneNumber");
		return firstTelephoneNumber;
	}

	private String organizationalUnits(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "organizationalUnits");
		System.out.println("Leído start tag organizationalUnits");
		String organizationalUnit = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("organizationalUnit"))
				if(organizationalUnit == null)
					organizationalUnit = readFirstOrganizationalUnit(parser);
				else readFirstOrganizationalUnit(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "organizationalUnits");
		System.out.println("Leído end tag organizationalUnits");
		return organizationalUnit;
	}
	
	private String readFirstOrganizationalUnit(XmlPullParser parser) throws XmlPullParserException, IOException {
		String organizationalUnit = null;
		parser.require(XmlPullParser.START_TAG, ns, "organizationalUnit");
		System.out.println("Leído start tag organizationalUnit");
		organizationalUnit = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "organizationalUnit");
		System.out.println("Leído end tag organizationalUnit");
		return organizationalUnit;
	}

	private String readPersonalWebAddress(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "personalWebAddresses");
		System.out.println("Leído start tag personalWebAddresses");
		String personalWebAddress = null;
		while(parser.next() != XmlPullParser.END_TAG) {
			if(parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			if(name.equals("personalWebAddress"))
				if(personalWebAddress == null)
					personalWebAddress = readFirstPersonalWebAddress(parser);
				else readFirstPersonalWebAddress(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, "personalWebAddresses");
		System.out.println("Leído end tag personalWebAddresses");
		return personalWebAddress;
	}
	
	private String readFirstPersonalWebAddress(XmlPullParser parser) throws XmlPullParserException, IOException {
		String personalWebAddress = null;
		parser.require(XmlPullParser.START_TAG, ns, "personalWebAddress");
		System.out.println("Leído start tag personalWebAddress");
		personalWebAddress = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "personalWebAddress");
		System.out.println("Leído end tag personalWebAddress");
		return personalWebAddress;
	}

	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
		String result = "";
		if(parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if(parser.getEventType() != XmlPullParser.START_TAG)
			throw new IllegalStateException();
		int depth = 1;
		while(depth != 0) {
			switch(parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
