package es.uji.PF.classes;

import java.io.Serializable;


//Commented
public class Place implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;

	//########################## Constructors ##########################
	
	/** 
	 * The default constructor.
	 * Initialize the name to "".
	 */
	Place(){
		setName("");
	}
	
	/** 
	 * Constructor.
	 * @param name The name of the place.
	 */
	Place(String name){
		setName(name);
	}

	//########################## Getters/Setters ##########################
	
	/**
	 * Getter
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//########################## Functions ##########################
	
	/**
	 * Function to get a string with the complete
	 * information about the person.
	 * @return A String with the complete description 
	 * of the place, using all the attributes.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Place name: "+name+ "<br />");
		return sb.toString();
	}
}
