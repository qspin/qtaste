package com.qspin.qtaste.sutfxdemo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

final class Person {

	public final static ObservableList<Person> DEFAULT_DATA = FXCollections.observableArrayList();
	static
	{
		DEFAULT_DATA.add(new Person("Mickey", "Mouse", 70, "Castel of Disney land"));
		DEFAULT_DATA.add(new Person("Tintin", "Milou", 40, "Moulinsart"));
		DEFAULT_DATA.add(new Person("Louis", "XVII", 30, "Versaille"));
		DEFAULT_DATA.add(new Person("Elisabeth", "II", 80, "London"));
		DEFAULT_DATA.add(new Person("Milou", "Tintin", 40, "Moulinsart"));
	}

	public Person(String pFirstName, String pLastName, int pAge, String pAdress)
	{
		setFirstName(new SimpleStringProperty(pFirstName));
		setLastName(new SimpleStringProperty(pLastName));
		setAge(new SimpleIntegerProperty(pAge));
		setAdress(new SimpleStringProperty(pAdress));
	}

	private StringProperty mFirstName;
	private StringProperty mLastName;
	private IntegerProperty mAge;
	private StringProperty mAdress;

	public String toString()
	{
		return "Firstname: " + getFirstName() + " | Lastname: " + getLastName() + " | Age: " + getAge() + " | Adress: " + getAdress();
	}

	public StringProperty getLastName() { return mLastName; }
	public StringProperty getFirstName() { return mFirstName; }
	public StringProperty getAdress() { return mAdress; }
	public IntegerProperty getAge() { return mAge; }

	public void setLastName(StringProperty pLastName) { mLastName = pLastName; }
	public void setFirstName(StringProperty pFirstName) { mFirstName = pFirstName; }
	public void setAdress(StringProperty pAdress) { mAdress = pAdress; }
	public void setAge(IntegerProperty pAge) { mAge = pAge; }
}
