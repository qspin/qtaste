package com.qspin.qtaste.sutuidemo;

final class Person {
	
	public Person(String pFirstName, String pLastName, int pAge, String pAdress)
	{
		setFirstName(pFirstName);
		setLastName(pLastName);
		setAge(pAge);
		setAdress(pAdress);
	}
	
	private String mFirstName;
	private String mLastName;
	private int mAge;
	private String mAdress;

	public String toString()
	{
		return "Firstname: " + getFirstName() + " | Lastname: " + getLastName() + " | Age: " + getAge() + " | Adress: " + getAdress();
	}
	
	public String getLastName() { return mLastName; }
	public String getFirstName() { return mFirstName; }
	public String getAdress() { return mAdress; }
	public int getAge() { return mAge; }

	public void setLastName(String pLastName) { mLastName = pLastName; }
	public void setFirstName(String pFirstName) { mFirstName = pFirstName; }
	public void setAdress(String pAdress) { mAdress = pAdress; }
	public void setAge(int pAge) { mAge = pAge; }
}
