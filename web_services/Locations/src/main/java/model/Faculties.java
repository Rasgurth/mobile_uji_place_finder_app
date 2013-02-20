package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Faculties {
	@XmlElement(name = "faculty")
	private List<Faculty> faculties;

	public Faculties() {
		super();
		faculties = new ArrayList<Faculty>();
	}

	public Faculties(List<Faculty> faculties) {
		super();
		this.faculties = faculties;
	}

	public void addFaculty(Faculty faculty) {
		if (faculties.contains(faculty) == false)
			faculties.add(faculty);
	}

	public List<Faculty> getFaculties() {
		return faculties;
	}

	public Faculty getFacultyByCommonName(List<String> newCommonNames) {
		for (Faculty faculty : faculties) {
			for (String commonName : faculty.getCommonNames())
				for (String newCommonName : newCommonNames)
					if (commonName.equals(newCommonName))
						return faculty;
		}
		return Faculty.NULL;
	}

	@Override
	public String toString() {
		return "Faculties [faculties=" + faculties + "]";
	}
}
