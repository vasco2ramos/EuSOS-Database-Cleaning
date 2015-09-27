
/**
*	Class that stores an email address and information pertaining to it.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
*/

public class Email{
	
	/** 
	*	Saves the ID as a string.
	**/
	private String _id;
	
	/** 
	*	Saves the name as a string.
	**/
	private String _name;
	
	/** 
	*	Saves the email address as an email.
	**/
	private String _email;
	
	/**
	*	Saves unique username.
	**/
	private String _username;
	
	/**
	*	Saves the doctor's location.
	**/
	private String _location;
	
	/**
	* Returns the stored ID.
	**/
	public String getID(){
		return _id;
	}
	
	/**
	* Returns the doctor's name.
	**/
	public String getName(){
		return _name;
	}
	
	/**
	* Returns the email address.
	**/
	public String getEmail(){
		return _email;
	}
	
	/**
	* Returns the unique username.
	**/
	public String getUsername(){
		return _username;
	}
	
	/**
	* Returns the doctor's location.
	**/
	public String getLocation(){
		return _location;
	}
	
	
	/**
	* Class constructor.
	* @param	id	Saves the doctor's ID
	* @param	name	Doctor's name
	* @param	email	Doctor's email address
	**/
	public Email(String id, String name, String email){
		_id = id;
		_name = name;
		_email = email;
	}
	
	
	/**
	* Class constructor.
	* @param	id	Saves the doctor's ID
	* @param	firstName	Doctor's first name
	* @param	lastName	Doctor's surname
	* @param	email	Doctor's email address
	**/
	public Email(String id, String firstName, String lastName, String email){
		_id = id;
		_name = firstName + " " + lastName;
		_email = email;
	}
	
	/**
	* Class constructor.
	* @param	id	Saves the doctor's ID
	* @param	name	Doctor's name
	* @param	email	Doctor's email address
	* @param	username	Doctor's username
	* @param	location	Doctor's location
	**/
	public Email(String id, String name, String email, String username, String location){
		_id = id;
		_name = name;
		_email = email;
		_username = username;
		_location = location;
	}
	
	/**
	*	Class writer.
	*/
	public String toString(){
		String output = "";
		output += "Doctor with username " + _username;
		output += " employed at " + _location;
		output += " with ID " + _id;
		return output;
	}
}