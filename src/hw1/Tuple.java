package hw1;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {
	
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	private TupleDesc td;
	private int pageId;
	private int id;
	public Map<Integer, Field> fields = new HashMap<Integer, Field>();
	
	
	public Tuple(TupleDesc t) {
		//your code here
		this.td = t;
	} 
	
	public TupleDesc getDesc() {
		//your code here
		return td;
		
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		return pageId;
	}

	public void setPid(int pid) {
		//your code here
		this.pageId = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		//your code here
		return id;
	}

	public void setId(int id) {
		//your code here
		this.id = id;
	}
	
	public void setDesc(TupleDesc td) {
		//your code here;
		this.td = td;
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		this.fields.put(i, v);
	}
	
	public Field getField(int i) {
		//your code here
		
		return fields.get(i);
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		//your code here 
		StringBuilder str = new StringBuilder();
		int length = fields.size();
		for (int i = 0; i<length; i++) {
			str.append(getField(i).toString());
		}
		String s = str.toString();
		return s;
	}
}