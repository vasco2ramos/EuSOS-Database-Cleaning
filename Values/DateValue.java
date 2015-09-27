package Values;

import java.util.Calendar;

/** 
*	Value for fields of the type "date".
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/

public class DateValue implements Value{

	/** 
	*	Saves a date Value.
	*	Calendar is used since Date has been deprecated.
	**/
	private Calendar _date;
	
	/**
	*	Returns the year of the saved date.
	**/
	public int getYear(){
		return _date.get(Calendar.YEAR);
	}

	/**
	*	Returns the month of the saved date.
	**/
	public int getMonth(){
		return _date.get(Calendar.MONTH);
	}

	/**
	*	Returns the day of the saved date.
	**/
	public int getDay(){
		return _date.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	*	Returns a calendar instance with the stored date.
	**/
	public Calendar getCalendar(){
		return _date;
	}
	
	/**
	*	Class constructor.
	*	@param	date	Date under the form of a"DD/MM/YYYY" string.
	**/
	public DateValue(String date){
		if(date.equals("")){
			_date = Calendar.getInstance();
			_date.set(0, 0, 0);
			return;
		}
		String[] splitDate = date.split("-");
		int year = 0, month = 0, day = 0;
		try {
			year = Integer.parseInt(splitDate[2]);
			month = Integer.parseInt(splitDate[1]);
			day = Integer.parseInt(splitDate[0]);
		} catch(NumberFormatException exception){
			System.err.println("### [DateValue] ERROR: unparseable integer in date ###");
		} catch(ArrayIndexOutOfBoundsException exception){
			System.err.println("### [DateValue] ERROR: unknown date format ###");
		}
		_date = Calendar.getInstance();
		_date.set(year, month, day);
	}
	
	/**
	*	Class printer
	**/
	public String toString(){
		return getDay() + "-" + getMonth() + "-" + getYear();
	}
	
}