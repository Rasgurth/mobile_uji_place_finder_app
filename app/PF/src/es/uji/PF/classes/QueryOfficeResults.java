package es.uji.PF.classes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//REVISAR!!!
public class QueryOfficeResults implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Office> offices;
	private int pagesNum;
	private int page;
	private int lastPageResults;

	//########################## Constructors ##########################
	
	/** 
	 * The default constructor.
	 * Initialize the query to "", 
	 * the offices array to null, 
	 * the officesNum to 0, 
	 * the page to 1 
	 * and calculate the rest of the attributes.
	 */
	public QueryOfficeResults(){
		this.offices=new ArrayList<Office>();
		calculatePages();
		setPage(1);
	}
	
	/** 
	 * Constructor.
	 * Initialize the query to "Persons",
	 * the page to 1,
	 * use a persons list to initialize the Offices array
	 * and calculate the rest of the attributes.
	 */
	public QueryOfficeResults(List<Person> persons){
		this.offices=new ArrayList<Office>();
		setOffices(persons);
		calculatePages();
		setPage(1);
	}

	//########################## Getters/Setters ##########################
	
	
	/**
	 * Getter.
	 * @return An array of offices, result of the query.
	 */
	public List<Office> getOffices() {
		return offices;
	}

	/**
	 * Getter.
	 * @return An array of offices, result of the query.
	 */
	public List<String> getOfficesRN() {
		List<String> rN=new ArrayList<String>();
		for(Office off: offices){
			rN.add(off.getRoomNumber());
		}
		return rN;
	}
	
	/**
	 * Setter.
	 * @param An array of offices, result of the query.
	 */
	public void setOffices(Office [] Off) {
		offices=new ArrayList<Office>();
		for(int i=0;i<Off.length;i++){
			addOffice(Off[i]);
		}
		calculatePages();
	}
	
	
	/**
	 * Setter.
	 * @param persons An array of persons, result of the query.
	 * With them the object populate the array of offices.
	 */
	public void setOffices(List<Person> persons) {
		offices=new ArrayList<Office>();
		boolean isNew,first=true;
		for(Person p: persons){
			if (first)
				addOffice(new Office(p));
			else{
				isNew=true;
				for(Office office: offices){
					if(office.getRoomNumber().equalsIgnoreCase(p.getRoomNumber())){
						addOcupant(getOfficesNum(),p);
						isNew=false;
						break;
					}
				}
				if(isNew){
					addOffice(new Office(p));
				}
			}
		}
		calculatePages();
	}

	public void addOcupant(int offNum, Person p) {
		Office auxO=offices.get(offNum);
		auxO.addOccupant(p);
		setOffice(auxO, offNum);
	}

	/**
	 * Getter.
	 * @return the number of offices.
	 */
	public int getOfficesNum() {
		return offices.size();
	}


	/**
	 * Setter.
	 * @param office The office to set.
	 * @param pos The position in the array of offices.
	 */
	public void setOffice(Office Office, int pos){
		if(getOfficesNum()<pos){
			offices.set(pos, Office);
		}
		else 
			offices.add(Office);
	}
	
	/**
	 * Getter.
	 * @param pos The position in the array of offices.
	 * @return The office indicated with the parameter pos.
	 */
	public Office getOffice(int pos){
		return offices.get(pos);
	}

	/**
	 * Getter.
	 * @return the pagesNum
	 */
	public int getPagesNum() {
		return pagesNum;
	}

	/**
	 * Setter.
	 * @param pagesNum the pagesNum to set
	 */
	public void setPagesNum(int pagesNum) {
		this.pagesNum = pagesNum;
	}

	/**
	 * Getter.
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Setter.
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Getter.
	 * @return the lastPageResults
	 */
	public int getLastPageResults() {
		return lastPageResults;
	}

	/**
	 * Setter.
	 * @param lastPageResults the lastPageResults to set
	 */
	public void setLastPageResults(int lastPageResults) {
		this.lastPageResults = lastPageResults;
	}
	
	/**
	 * Setter.
	 * @param rN The room number of the office to set its building
	 * @param building The building to set.
	 */
	public void setBuilding(String rN,String building) {
		int i=0;
		for(Office off: offices){
			if(off.getRoomNumber().equalsIgnoreCase(rN)){
				off.setBuilding(building);
				setOffice(off,i);
				break;
			}else{
				i++;
			}
		}
	}
	
	/**
	 * Setter.
	 * @param rN The room number of the office
	 * to set its floor.
	 * @param floor The floor to set.
	 */
	public void setFloor(String rN,int floor) {
		int i=0;
		for(Office off: offices){
			if(off.getRoomNumber().equalsIgnoreCase(rN)){
				off.setFloor(floor);
				setOffice(off,i);
				break;
			}else{
				i++;
			}
		}
	}
	
	/**
	 * Setter.
	 * @param rN The room number of the office 
	 * to set its building and floor.
	 * @param building The building to set.
	 * @param floor The floor to set.
	 */
	public void setLoc(String rN,String building,int floor) {
		int i=0;
		for(Office off: offices){
			if(off.getRoomNumber().equalsIgnoreCase(rN)){
				off.setBuilding(building);
				off.setFloor(floor);
				setOffice(off,i);
				break;
			}else{
				i++;
			}
		}
	}

	
	//########################## Functions ##########################
	
	/**
	 * Add an office to the offices array,
	 * and increment the officesNum.
	 * @param office The offices to add.
	 */
	public void addOffice(Office office){
		offices.add(office);
		calculatePages();
	}
	
	public int isIn(Office office){
		int i=1,pos=-1;
		String aux=null;
		String officeRN=""+office.getRoomNumber();
		if(offices.size()==0)
			return pos;
		
		for(Office off: offices){
			aux=""+off.getRoomNumber();
			System.out.println("IsIn(off "+i+"): aux="+aux);
			if(aux.equalsIgnoreCase("null")){
				for(Person occ: off.getOccupants()){
					System.out.println("IsIn(off "+i+"): aux="+aux+"; occ.name="+occ.getName());
					for(Person occOff: office.getOccupants()){
						if(occ.getName().equalsIgnoreCase(occOff.getName())){
							pos=i;
						}
					}
				}
			}else{
				if(aux.equalsIgnoreCase(officeRN)){
					pos=i;
				}
			}
			i++;
		}
		return pos;
	}
	
	/**
	 * Delete an office of the offices array.
	 * @param office The offices to delete.
	 */
	public void delOffice(Office office){
		Office [] aux=new Office[offices.size()-1];
		String officeRN=""+office.getRoomNumber();
		String offRN;
		int i=0;
		for(Office off: offices){
			offRN=""+off.getRoomNumber();
			if(!offRN.equalsIgnoreCase(officeRN)){
				aux[i]=off;
				i++;
			}else if(officeRN.equalsIgnoreCase("null")){
				if(!off.getOccupant(0).getName().equalsIgnoreCase(office.getOccupant(0).getName())){
					aux[i]=off;
					i++;
				}
				
			}
		}
		setOffices(aux);
	}
	
	/**
	 * Increment the Page variable.
	 */
	public void incrPage(){
		setPage(getPage()+1);
	}
	
	/**
	 * Decrement the Page variable.
	 */
	public void decrPage(){
		setPage(getPage()-1);
	}
	
	/**
	 * Save a QueryOfficeResults in a file.
	 * @param fos The file where the object will be saved.
	 * @param q The object to save.
	 */
	public void save(FileOutputStream fos, QueryOfficeResults q){
		ObjectOutputStream oos=null;
		
		try {
			oos = new ObjectOutputStream(fos);
			oos.writeObject(q);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a QueryOfficeResults from a file.
	 * @param fis The file from where the object will be loaded.
	 * @return The load object.
	 */
	public QueryOfficeResults load(FileInputStream fis){
		QueryOfficeResults aux = new QueryOfficeResults();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(fis);
			aux= (QueryOfficeResults) is.readObject();
		
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aux;
	}

	/**
	 * Calculate the number of pages to show the results,
	 * and the number of results to show with the last page.
	 */
	public void calculatePages(){
		int rest=getOfficesNum()%4;
		int res=getOfficesNum()/4;
		if(rest==0){
			setPagesNum(res);
			setLastPageResults(4);
		}else{
			setPagesNum(res+1);
			setLastPageResults(rest);
			
		}
	}
	

	/**
	 * Return a list with the offices to show in a certain
	 * results page.
	 * @param page The results page number where 
	 * the offices will be showed
	 * @return A list of offices. The number of offices
	 * inside the list depends on whether it is to show
	 * the last page (should be at least 1 and at most 4),
	 * or are from another page (shown 4 offices per page).
	 */
	public List<Office> getResultsPage(int page){
		List<Office> aux= new ArrayList<Office>();
		if(page<getPagesNum()){
			for(int i=0;i<4;i++){
				aux.add(getOffice(i+(page-1)*4));
			}
		}else if(page==getPagesNum()){
			for(int i=0;i<getLastPageResults();i++){
				aux.add(getOffice(i+(page-1)*4));
			}
		}
		return aux;
	}
	
	/**
	 * Return a list with the offices to show in the actual
	 * results page.
	 * @return A list of offices. The number of offices
	 * inside the list depends on whether it is to show
	 * the last page (should be at least 1 and at most 4),
	 * or are from another page (shown 4 offices per page).
	 */
	public List<Office> getResultsActualPage(){
		return getResultsPage(getPage());
	}

	/**
	 * Increase the page counter.
	 */
	public void nextPage(){
		setPage(getPage()+1);
	}

}
