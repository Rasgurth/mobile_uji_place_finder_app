package es.uji.PF.classes;

import java.io.Serializable;

//Commented
public class GenericRoom extends Place implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roomNumber;
	private String building;
	private int floor;

	//########################## Constructors ##########################

	/** 
	 * The default constructor. Initialize 
	 * the attributes with a default value
	 * (and the name "room" for a GenericRoom).
	 */
	public GenericRoom(){
		super("room");
		setRoomNumber("");
		setBuilding("");
		setFloor(-1);
	}

	/** 
	 * Constructor. Initialize the room number
	 * with the indicated value, and the rest of
	 * attributes with a default value
	 * (and the name "room" for a GenericRoom).
	 * @param rN The room number 
	 * to identify the office.
	 */
	public GenericRoom(String rN){
		super("room");
		setRoomNumber(rN);
		setBuilding("");
		setFloor(0);
	}
	
	/** 
	 * Constructor. Initialize the room number and the
	 * building with the indicated values, 
	 * and the rest of attributes with a default value
	 * (and the name "room" for a GenericRoom).
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 */
	public GenericRoom(String rN, String bldng){
		super("room");
		setRoomNumber(rN);
		setBuilding(bldng);
		setFloor(0);
	}
	
	/** 
	 * Constructor. Initialize the room number, 
	 * the building and the floor with  
	 * the indicated values, and the attributes
	 * with a default value
	 * (and the name "room" for a GenericRoom).
	 * @param rN The room number 
	 * to identify the office.
	 * @param bldng The name of the building 
	 * where the office is located.
	 * @param flr The floor where
	 * the office is located.
	 */
	public GenericRoom(String rN, String bldng, int flr){
		super("room");
		setRoomNumber(rN);
		setBuilding(bldng);
		setFloor(flr);
	}

	//########################## Getters/Setters ##########################
	
	/**
	 * Getter.
	 * @return The roomNumber to identify the room.
	 */
	public String getRoomNumber() {
		return roomNumber;
	}

	/**
	 * Setter.
	 * @param roomNumber The roomNumber to identify the room.
	 */
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	/**
	 * Getter.
	 * @return The building where the room is.
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * Setter.
	 * @param building The building where the room is.
	 */
	public void setBuilding(String building) {
		this.building = building;
	}

	/**
	 * Getter.
	 * @return The floor where the room is.
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Setter.
	 * @param floor The floor where the room is.
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	//########################## Functions ##########################

	/**
	 * Function to get a string with the complete
	 * information about the room.
	 * @return The complete description 
	 * of the room, using all the attributes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		if(!getRoomNumber().equalsIgnoreCase(""))
			sb.append("Numero de habitación: " + getRoomNumber() + "<br />");
		if(!getBuilding().equalsIgnoreCase(""))
			sb.append("Edificio: " + getBuilding() + "<br />");
		if(getFloor()!=-1)
			sb.append("Piso: " + getFloor() + "<br />");

		return sb.toString();
	}

}
