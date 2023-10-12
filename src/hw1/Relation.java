package hw1;

import java.util.ArrayList;

/**
 * This class provides methods to perform relational algebra operations. It will
 * be used to implement SQL queries.
 * 
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;

	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		// your code here
		this.tuples = l;
		this.td = td;
	}

	/**
	 * This method performs a select operation on a relation
	 * 
	 * @param field   number (refer to TupleDesc) of the field to be compared, left
	 *                side of comparison
	 * @param op      the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		// your code here

		// return null;
		ArrayList<Tuple> selectedTuples = new ArrayList<>();

		for (Tuple tuple : tuples) {

			Field tupleField = tuple.getField(field);

			// Check if the comparison condition holds
			if (tupleField.compare(op, operand)) {
				selectedTuples.add(tuple);
			}
		}
		TupleDesc newTd = td;
		return new Relation(selectedTuples, newTd);
	}

	/**
	 * This method performs a rename operation on a relation
	 * 
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be
	 *               renamed
	 * @param names  a list of new names. The order of these names is the same as
	 *               the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) {
		// your code here
		// return null;

		String[] newFields = new String[td.numFields()];
		newFields = td.getFields();
		Type[] arr = td.getType();

		for (int i = 0; i < fields.size(); i++) {
			newFields[fields.get(i)] = names.get(i);
		}

		TupleDesc newTd = new TupleDesc(arr, newFields);
		// td.setFields(newFields);
		return new Relation(tuples, newTd);
	}

	/**
	 * This method performs a project operation on a relation
	 * 
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in
	 *               the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		// your code here
//		return null;
		ArrayList<Tuple> tuples = new ArrayList<>();

		Type[] type = new Type[fields.size()];
		String[] fieldArr = new String[fields.size()];
		int i = 0;
		int j = 0;
		for (Integer integer : fields) {
			type[i] = this.td.getType(integer);
			fieldArr[i] = this.td.getFieldName(integer);
			i++;
		}

		TupleDesc newTd = new TupleDesc(type, fieldArr);

		for (Tuple tuple : this.getTuples()) {
			Tuple tup = new Tuple(newTd);
			for (Integer integer : fields) {

				tup.setField(j, tuple.getField(integer));
				j++;

			}
			tuples.add(tup);
		}
		return new Relation(tuples, newTd);

	}

	/**
	 * This method performs a join between this relation and a second relation. The
	 * resulting relation will contain all of the columns from both of the given
	 * relations, joined using the equality operator (=)
	 * 
	 * @param other  the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be
	 *               used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in
	 *               the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		// your code here
		int totalLength = this.td.getNumFields() + other.td.getNumFields();
		Type[] type = new Type[this.td.getNumFields() + other.td.getNumFields()];
		String[] fieldArr = new String[this.td.getNumFields() + other.td.getNumFields()];
		for (int i = 0; i < this.td.getNumFields(); i++) {
			type[i] = this.td.getType(i);
			fieldArr[i] = this.td.getFieldName(i);
		}
		for (int j = this.td.getNumFields(); j < totalLength; j++) {
			type[j] = other.td.getType(j - this.td.getNumFields());
			fieldArr[j] = other.td.getFieldName(j - this.td.getNumFields());
		}

		TupleDesc tp = new TupleDesc(type, fieldArr);

		ArrayList<Tuple> tupList = new ArrayList<Tuple>();
		for (Tuple tuple : this.getTuples()) {
			for (Tuple tuple2 : other.getTuples()) {
				if (tuple.getField(field1).equals(tuple2.getField(field2))) {
					Tuple tup = new Tuple(tp);
					for (int i = 0; i < this.td.getNumFields(); i++) {
						tup.setField(i, tuple.getField(i));
					}
					for (int j = this.td.getNumFields(); j < totalLength; j++) {
						tup.setField(j, tuple2.getField(j - this.td.getNumFields()));
					}
					tupList.add(tup);
				}
			}
		}

		return new Relation(tupList, tp);

	}

	/**
	 * Performs an aggregation operation on a relation. See the lab write up for
	 * details.
	 * 
	 * @param op      the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		// ArrayList<Tuple> tuples = new ArrayList<>();
		// TupleDesc newTd = new TupleDesc();

		// use aggregator after it is finished
		Aggregator a = new Aggregator(op, groupBy, this.td);
		for(Tuple t: tuples) {
			a.merge(t);
		}
		ArrayList<Tuple> t = a.getResults();
		return new Relation(a.getResults(), this.td);
	}

	public TupleDesc getDesc() {
		// your code here
		return td;
	}

	public ArrayList<Tuple> getTuples() {
		// your code here
		return tuples;
	}

	/**
	 * Returns a string representation of this relation. The string representation
	 * should first contain the TupleDesc, followed by each of the tuples in this
	 * relation
	 */
	public String toString() {
		// your code here
		// print all tuples
		return null;
	}
}
