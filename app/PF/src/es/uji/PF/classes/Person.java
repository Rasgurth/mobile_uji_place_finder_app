package es.uji.PF.classes;

import java.io.Serializable;

//commented
public class Person implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String mail;
	private String roomNumber;
	private String telephoneNumber;
	private String organizationalUnit;
	private String personalWebAddress;

	//########################## Constructors ##########################

	/** 
	 * The default constructor. Initialize the attributes to "".
	 */
	public Person(){
		setName("");
		setDescription("");
		setMail("");
		setRoomNumber("");
		setTelephoneNumber("");
		setOrganizationalUnit("");
		setPersonalWebAddress("");
	}

	/** 
	 * Constructor. Initialize the attributes
	 * with the obtained parameters.
	 * @param name the name of the person
	 * @param description the description of the person
	 * @param mail the mail address of the person
	 * @param roomNumber the code to identify his/her office
	 * @param telephoneNumber the telephone number of his/her office
	 * @param organizationalUnit the organizational unit of the person
	 * @param personalWebAddress the personal web address
	 */
	public Person(String name, String description, String mail, 
			String roomNumber, String telephoneNumber, 
			String organizationalUnit, String personalWebAddress) {
		setName(name);
		setDescription(description);
		setMail(mail);
		setRoomNumber(roomNumber);
		setTelephoneNumber(telephoneNumber);
		setOrganizationalUnit(organizationalUnit);
		setPersonalWebAddress(personalWebAddress);
	}

	//########################## Getters/Setters ##########################
	
	/**
	 * Getter.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Setter.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Setter.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Getter.
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}
	
	/**
	 * Setter.
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	/**
	 * Getter.
	 * @return the roomNumber
	 */
	public String getRoomNumber() {
		return roomNumber;
	}
	/**
	 * Setter.
	 * @param roomNumber the roomNumber to set
	 */
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	/**
	 * Getter.
	 * @return the telephoneNumber
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	/**
	 * Setter.
	 * @param telephoneNumber the telephoneNumber to set
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	
	/**
	 * Getter.
	 * @return the organizationalUnit
	 */
	public String getOrganizationalUnit() {
		return organizationalUnit;
	}
	
	/**
	 * Setter.
	 * @param organizationalUnit the organizationalUnit to set
	 */
	public void setOrganizationalUnit(String organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}
	
	/**
	 * Getter.
	 * @return the personalWebAddress
	 */
	public String getPersonalWebAddress() {
		return personalWebAddress;
	}
	
	/**
	 * Setter.
	 * @param personalWebAddress the personalWebAddress to set
	 */
	public void setPersonalWebAddress(String personalWebAddress) {
		this.personalWebAddress = personalWebAddress;
	}

	//########################## Functions ##########################
	
	/**
	 * Function to get a string with the complete
	 * information about the person.
	 * @return The complete description 
	 * of the person, using all the attributes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(name != null)
			sb.append("<b><i>Nombre:</i></b> " + name + "<br />");
		if(description != null)
			sb.append("<b><i>Puesto:</i></b> "+ description + "<br />");
		if(mail != null)
			sb.append("<b><i>e-mail:</i></b> <a href='mailto:" + mail + "'>" + 
						mail + "</a> <br />");
		if(roomNumber != null)
			sb.append("<b><i>Ubicación:</i></b> " + roomNumber);
		if(organizationalUnit != null)
			if(roomNumber == null)
				sb.append("<b><i>UbicaciÃ³n:</i></b> " + organizationalUnit + "<br />");
			else
				sb.append(", " + organizationalUnit + "<br />");
		if(telephoneNumber != null)
			sb.append("<b><i>Teléfono:</i></b> " + telephoneNumber + "<br />");
		if(personalWebAddress != null)
			sb.append("<b><i>Página web:</i></b> <a href='" + personalWebAddress + "'>" +
					  personalWebAddress + "</a> <br />");
		return sb.toString();
	}
	
	
}
