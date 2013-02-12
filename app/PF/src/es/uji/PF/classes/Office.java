package es.uji.PF.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Commented
public class Office extends GenericRoom implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Person> occupants;

	//########################## Constructors ##########################

	/** 
	 * The default constructor. Initialize 
	 * the attributes with a default value.
	 */
	public Office(){
		super();
		iniOccupants();
	}

	/** 
	 * Constructor. Initialize the room number
	 * with the indicated value, and the attributes
	 * with a default value.
	 * @param rN The room number 
	 * to identify the office.
	 */
	public Office(String rN){
		super(rN);
		iniOccupants();
	}

	/** 
	 * Constructor. Initialize the room number and the
	 * building with the indicated values, 
	 * and the attributes with a default value.
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 */
	public Office(String rN, String bldng){
		super(rN,bldng);
		iniOccupants();
	}

	/** 
	 * Constructor. Initialize the room number, 
	 * the building and the floor with  
	 * the indicated values, and the attributes
	 * with a default value.
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 * @param flr The floor where
	 * the office is located.
	 */
	public Office(String rN, String bldng, int flr){
		super(rN,bldng,flr);
		iniOccupants();
	}
	
	/** 
	 * Constructor. Initialize the room number, 
	 * the building, the floor and one occupant with  
	 * the indicated values, and the attributes
	 * with a default value.
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 * @param flr The floor where
	 * the office is located.
	 * @param occupant One occupant of the office.
	 */
	public Office(String rN, String bldng, int flr, Person occupant){
		super(rN,bldng,flr);
		iniOccupants();
		addOccupant(occupant);
	}

	/** 
	 * Constructor. Initialize the room number, 
	 * the building, the floor and one occupant with  
	 * the indicated values, and the attributes
	 * with a default value.
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 * @param flr The floor where
	 * the office is located.
	 * @param occupants The occupants of the office.
	 * @param cant One occupant of the office.
	 */
	public Office(String rN, String bldng, int flr, Person [] occupants, int cant){
		super(rN,bldng,flr);
		iniOccupants();
		setOccupants(occupants);
	}
	
	/** 
	 * Constructor. Initialize the room number, 
	 * the building, the floor and one occupant with  
	 * the indicated values, and the attributes
	 * with a default value.
	 * @param occupant The occupant of the office.
	 * This occupant has the room number of his office,
	 * and it can be used to identify this office. After it,
	 * initialize the occupants array and add the occupant.
	 */
	public Office(Person occupant){
		super(occupant.getRoomNumber());
		iniOccupants();
		addOccupant(occupant);
	}
	
	public Office(GenericRoom office) {
		super(office.getRoomNumber());
		iniOccupants();
	}
	
	//########################## Initializers ##########################

	/**
	 * Initialize the occupants list.
	 */
	public void iniOccupants(){
		occupants=new ArrayList<Person>();
	}

	//########################## Getters/Setters ##########################
	
	/**
	 * Getter.
	 * @return The number of occupants
	 */
	public int getOccNum(){
		return occupants.size();
	}

	/**
	 * Getter.
	 * @return A list with the of occupants.
	 */
	public List<Person> getOccupants(){
		return occupants;
	}
	
	/**
	 * Setter.
	 * @param group Array with the occupants.
	 */
	public void setOccupants(Person[] group){
		for(int i=0;i<group.length;i++){
			setOccupant(group[i],i);
		}
	}

	/**
	 * Getter.
	 * @param pos The position of the occupant to get.
	 * @return The person with the indicated position
	 * inside the array of occupants.
	 */
	public Person getOccupant(int pos){
		return occupants.get(pos);
	}

	/**
	 * Setter.
	 * If the pos is out of range, add the occupant 
	 * into the last position.
	 * @param occupant The occupant to set.
	 * @param pos The position in the list of occupants.
	 */
	public void setOccupant(Person occupant, int pos){
		if(occupants.size()<pos){
			occupants.set(pos, occupant);
		}
		else 
			occupants.add(occupant);
	}

	//########################## Functions ##########################
	
	/**
	 * Add an occupant to the occupants list,
	 * into the last position.
	 * @param office The offices to add.
	 */
	public void addOccupant(Person occupant){
		setOccupant(occupant,getOccNum());
	}

	/**
	 * Function to get a string with the complete
	 * information about the office.
	 * @return The complete description 
	 * of the office, using all the attributes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append(super.toString());
		for(int i=0;i<getOccNum();i++){
			sb.append("----- <br />");
			sb.append(getOccupant(i).toString());
		}
		return sb.toString();
	}
}
