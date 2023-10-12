package hw1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
	private AggregateOperator o;
	private boolean groupBy;
	private TupleDesc td;
	private ArrayList<Tuple> tuples;
	int total = 0;
	boolean IntField;

	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		//your code here
		this.o = o;
		this.groupBy = groupBy;
		this.td = td;
		this.IntField = true;
		this.tuples = new ArrayList<Tuple>();
		
	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		if(this.groupBy) {
			this.tuples.add(t);
		}
		else {
			if(t.getDesc().getType(0) != Type.INT) {
				this.IntField = false;
			}
			else {
				this.IntField = true;
			}
			
			if(this.o == AggregateOperator.AVG) {
				this.total++;
			}
			this.tuples.add(t);
		}
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		//your code here
		return tuples;
		
	}

}
