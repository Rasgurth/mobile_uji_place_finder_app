package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Faculty {
	@XmlTransient
	public static final Faculty NULL = new Faculty();
	@XmlElementWrapper(name="commonNames")
	@XmlElement(name="commonName")
	private List<String> commonNames;
	@XmlElementWrapper(name="descriptions")
	@XmlElement(name="description")
	private List<String> descriptions;
	@XmlElementWrapper(name="mails")
	@XmlElement(name="mail")
	private List<String> mails;
	@XmlElementWrapper(name="roomNumbers")
	@XmlElement(name="roomNumber")
	private List<String> roomNumbers;
	@XmlElementWrapper(name="telephoneNumbers")
	@XmlElement(name="telephoneNumber")
	private List<String> telephoneNumbers;
	@XmlElementWrapper(name="uids")
	@XmlElement(name="uid")
	private List<String> uids;
	@XmlElementWrapper(name="organizationalUnits")
	@XmlElement(name="organizationalUnit")
	private List<String> organizationalUnits;
	@XmlElementWrapper(name="personalWebAddresses")
	@XmlElement(name="personalWebAddress")
	private List<String> personalWebAddresses;
	
	public Faculty() {
		super();
		commonNames = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		mails = new ArrayList<String>();
		roomNumbers = new ArrayList<String>();
		telephoneNumbers = new ArrayList<String>();
		uids = new ArrayList<String>();
		organizationalUnits = new ArrayList<String>();
		personalWebAddresses = new ArrayList<String>();
	}

	public Faculty(List<String> commonName, List<String> description,
			List<String> mail, List<String> roomNumber,
			List<String> telephoneNumber, List<String> uid,
			List<String> organizationalUnit, List<String> personalWebAddress) {
		super();
		this.commonNames = commonName;
		this.descriptions = description;
		this.mails = mail;
		this.roomNumbers = roomNumber;
		this.telephoneNumbers = telephoneNumber;
		this.uids = uid;
		this.organizationalUnits = organizationalUnit;
		this.personalWebAddresses = personalWebAddress;
	}

	public List<String> getCommonNames() {
		return commonNames;
	}

	public void setCommonNames(List<String> commonNames) {
		this.commonNames = commonNames;
	}
	
	public void addCommonName(List<String> newCcommonNames) {
		for(String newCommonName: newCcommonNames)
		if(newCommonName.equals("") == false && commonNames.contains(newCommonName) == false)
			commonNames.add(newCommonName);
	}
	
	public boolean isNewCommonName(List<String> newCommonNames) {
		for(String newCommonName: newCommonNames)
			if(newCommonName.equals("") == false && commonNames.contains(newCommonName) == false)
				return true;
		return false;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public void addDescription(List<String> newDescriptions) {
		for(String newDescription: newDescriptions)
		if(newDescription.equals("") == false && descriptions.contains(newDescription) == false)
			descriptions.add(newDescription);
	}

	public List<String> getMails() {
		return mails;
	}

	public void setMails(List<String> mails) {
		this.mails = mails;
	}
	
	public void addMail(List<String> newMails) {
		for(String newMail: newMails)
		if(newMail.equals("") == false && mails.contains(newMail) == false)
			mails.add(newMail);
	}
	
	public List<String> getRoomNumbers() {
		return roomNumbers;
	}

	public void setRoomNumbers(List<String> roomNumbers) {
		this.roomNumbers = roomNumbers;
	}
	
	public void addRoomNumber(List<String> newRoomNumbers) {
		for(String newRoomNumber: newRoomNumbers)
		if(newRoomNumber.equals("") == false && roomNumbers.contains(newRoomNumber) == false)
			roomNumbers.add(newRoomNumber);
	}

	public List<String> getTelephoneNumbers() {
		return telephoneNumbers;
	}

	public void setTelephoneNumbers(List<String> telephoneNumbers) {
		this.telephoneNumbers = telephoneNumbers;
	}
	
	public void addTelephoneNumber(List<String> newTelephoneNumbers) {
		for(String newTelephoneNumber: newTelephoneNumbers)
		if(newTelephoneNumber.equals("") == false && telephoneNumbers.contains(newTelephoneNumber) == false)
			telephoneNumbers.add(newTelephoneNumber);
	}

	public List<String> getUids() {
		return uids;
	}

	public void setUids(List<String> uids) {
		this.uids = uids;
	}
	
	public void addUid(List<String> newUids) {
		for(String newUid: newUids)
		if(newUid.equals("") == false && uids.contains(newUid) == false)
			uids.add(newUid);
	}

	public List<String> getOrganizationalUnits() {
		return organizationalUnits;
	}

	public void setOrganizationalUnits(List<String> organizationalUnits) {
		this.organizationalUnits = organizationalUnits;
	}
	
	public void addOrganizationalUnit(List<String> newOrganizationalUnits) {
		for(String newOrganizationalUnit: newOrganizationalUnits)
		if(newOrganizationalUnit.equals("") == false && organizationalUnits.contains(newOrganizationalUnit) == false)
			organizationalUnits.add(newOrganizationalUnit);
	}

	public List<String> getPersonalWebAddresses() {
		return personalWebAddresses;
	}

	public void setPersonalWebAddresses(List<String> personalWebAddresses) {
		this.personalWebAddresses = personalWebAddresses;
	}
	
	public void addPersonalWebAddress(List<String> newWebAddresses) {
		for(String newWebAddress: newWebAddresses)
		if(newWebAddress.equals("") == false && personalWebAddresses.contains(newWebAddress) == false)
			personalWebAddresses.add(newWebAddress);
	}

	@Override
	public String toString() {
		return "Faculty [commonNames=" + commonNames + ", descriptions="
				+ descriptions + ", mails=" + mails + ", roomNumbers=" + roomNumbers
				+ ", telephoneNumbesr=" + telephoneNumbers + ", uids=" + uids
				+ ", organizationalUnits=" + organizationalUnits
				+ ", personalWebAddresses=" + personalWebAddresses + "]";
	}

}
