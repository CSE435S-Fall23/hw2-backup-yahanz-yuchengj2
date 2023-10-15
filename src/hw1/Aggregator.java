package hw1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * 
 * @author Doug Shook
 *
 */
public class Aggregator {
	private AggregateOperator aggregateOp;
	private boolean group;
	private TupleDesc tupleDesc;
	private Map<Object, Object> tuples;
	private Map<Object, Integer> ct;

	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		this.aggregateOp = o;
		this.group = groupBy;
		this.tupleDesc = td;
		this.tuples = new HashMap<>();
		this.ct = new HashMap<>();
	}

	public void merge(Tuple t) {
		if (!group) {
			if (tupleDesc.getType(0) == Type.INT) {
				int value = ((IntField) t.getField(0)).getValue();
				if (tuples.get(0) == null) {
					tuples.put(0, value);
				} else if (tuples.get(0) != null) {
					int thisValue = (int) tuples.get(0);
					if (aggregateOp == aggregateOp.SUM) {
						tuples.put(0, thisValue + value);
					}

					else if (aggregateOp == aggregateOp.MAX) {
						if (value > thisValue) {
							tuples.put(0, value);
						}
					}

					else if (aggregateOp == aggregateOp.MIN) {
						if (value < thisValue) {
							tuples.put(0, value);
						}
					}

					else if (aggregateOp == aggregateOp.COUNT) {
						tuples.put(0, (int) tuples.get(0) + 1);
					}

				}
			} else if (tupleDesc.getType(0) == Type.STRING) {
				String value = ((StringField) t.getField(0)).getValue();
				if (tuples.get(0) != null) {
					String thisValue = (String) tuples.get(0);
					int result = value.compareTo(thisValue);
					if (aggregateOp == aggregateOp.MAX) {

						if (result > 0) {
							tuples.put(0, value);
						}
					}

					else if (aggregateOp == aggregateOp.MIN) {
						if (result < 0) {
							tuples.put(0, value);
						}
					} else if (aggregateOp == aggregateOp.COUNT) {
						tuples.put(0, (int) tuples.get(0) + 1);
					}

				} else {
					tuples.put(0, value);
				}
			}
			
		} else {
			
			Object thisKey = ((IntField) t.getField(0)).getValue();
			Object value = ((IntField) t.getField(1)).getValue();
			if (tupleDesc.getType(1) == Type.INT) {
				if (!tuples.containsKey(thisKey)) {
					tuples.put(thisKey, value);
				} else {
					int currentValue = (int) tuples.get(thisKey);

					if (aggregateOp == aggregateOp.MAX) {
						if ((int) value > currentValue) {
							tuples.put(thisKey, value);
						}
					} else if (aggregateOp == aggregateOp.MIN) {
						if ((int) value < currentValue) {
							tuples.put(thisKey, value);
						}
					} else if (aggregateOp == aggregateOp.SUM) {
						tuples.put(thisKey, currentValue + (int) value);
					} else if (aggregateOp == aggregateOp.COUNT) {
						tuples.put(thisKey, (int) tuples.get(thisKey) + 1);
					}

				}
			} else if (tupleDesc.getType(1) == Type.STRING) {
				
				String currentValue = (String) tuples.get(thisKey);
				switch (aggregateOp) {
				case MAX:
					if (value.toString().compareTo(currentValue) > 0) {
						tuples.put(thisKey, value);
					}
					break;
				case MIN:
					if (value.toString().compareTo(currentValue) < 0) {
						tuples.put(thisKey, value);
					}
					break;
				case COUNT:
					tuples.put(thisKey, (int) tuples.get(thisKey) + 1);
					break;
				}
			}

			
			if (ct.containsKey(thisKey)) {
				ct.put(thisKey, ct.get(thisKey) + 1);
			} else {
				ct.put(thisKey, 1);
			}
		}
	}

	public ArrayList<Tuple> getResults() {
		ArrayList<Tuple> myTuples = new ArrayList<>();

		if (!group) {
			Type type = tupleDesc.getType(0);
			int index = (int) tuples.get(0);
			Tuple tup = new Tuple(tupleDesc);
			if (type == Type.INT) {
				int aggregatedValue = index;
				tup.setField(0, new IntField(aggregatedValue));
			} else if (type == Type.STRING) {
				String aggregatedValue = (String) tuples.get(0);
				tup.setField(0, new StringField(aggregatedValue));
			}
			myTuples.add(tup);
		} else {
			for (Object groupKey : tuples.keySet()) {
				Tuple tup = new Tuple(tupleDesc);
				if (tupleDesc.getType(0) == Type.INT) {
					IntField field = new IntField((int) groupKey);
					tup.setField(0, field);
				} else if (tupleDesc.getType(0) == Type.STRING) {
					StringField s = new StringField(groupKey.toString());
					tup.setField(0, s);
				}

				if (tupleDesc.getType(1) == Type.INT) {
					int aggregatedValue = (int) tuples.get(groupKey);
					tup.setField(1, new IntField(aggregatedValue));
				} else if (tupleDesc.getType(1) == Type.STRING) {
					String aggValue = (String) tuples.get(groupKey);
					tup.setField(1, new StringField(aggValue));
				}

				myTuples.add(tup);
			}
		}

		return myTuples;
	}
}
