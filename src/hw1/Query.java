package hw1;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Query {

	private String q;

	public Query(String q) {
		this.q = q;
	}

	public Relation execute() {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect) selectStatement.getSelectBody();

		// your code here
		Catalog cata = Database.getCatalog();
		ColumnVisitor colVisitor = new ColumnVisitor();
		TablesNamesFinder tNameFinder = new TablesNamesFinder();
		List<String> tList = tNameFinder.getTableList(statement);

		int tbId = cata.getTableId(tList.get(0));
		ArrayList<Tuple> tupleList = cata.getDbFile(tbId).getAllTuples();
		TupleDesc initialTd = cata.getTupleDesc(tbId);
		Relation theRelation = new Relation(tupleList, initialTd);

		// join
		Relation joinRelation = theRelation;
		List<Join> joinList = sb.getJoins();

		if (joinList != null) {
			for (Join join : joinList) {
				String tableName = join.getRightItem().toString();

				TupleDesc joinTupleDesc = cata.getTupleDesc(cata.getTableId(tableName));
				ArrayList<Tuple> joinTupleList = cata.getDbFile(cata.getTableId(tableName)).getAllTuples();

				Relation newJoinRel = new Relation(joinTupleList, joinTupleDesc);

				String[] splitOnExpression = join.getOnExpression().toString().split("=");

				String[] splitField1 = splitOnExpression[0].trim().split("\\.");
				String[] splitField2 = splitOnExpression[1].trim().split("\\.");

				String tableName2 = splitField2[0];
				String fieldName1 = splitField1[1];
				String fieldName2 = splitField2[1];

				if (!tableName.toLowerCase().equals(tableName2.toLowerCase())) {
					String temp = fieldName1;
					fieldName1 = fieldName2;
					fieldName2 = temp;
				}

				int fieldIndex1 = joinRelation.getDesc().nameToId(fieldName1);
				int fieldIndex2 = newJoinRel.getDesc().nameToId(fieldName2);

				joinRelation = joinRelation.join(newJoinRel, fieldIndex1, fieldIndex2);
			}
		}

		// where
		Relation whereRelation = joinRelation;
		WhereExpressionVisitor whereVisitor = new WhereExpressionVisitor();
		if (sb.getWhere() != null) {
			sb.getWhere().accept(whereVisitor);
			whereRelation = joinRelation.select(joinRelation.getDesc().nameToId(whereVisitor.getLeft()),
					whereVisitor.getOp(), whereVisitor.getRight());
		}

		// select
		Relation selectRelation = whereRelation;
		List<SelectItem> selectList = sb.getSelectItems();

		ArrayList<Integer> fieldList = new ArrayList<Integer>();

		for (SelectItem item : selectList) {
			item.accept(colVisitor);

			String colSelect;
			if(colVisitor.isAggregate() == true) {
				colSelect = colVisitor.getColumn();
			}
			else {
				
				colSelect = item.toString();
			}

			if (colSelect.equals("*")) {
				for (int i = 0; i < whereRelation.getDesc().numFields(); i++) {
					fieldList.add(i);
				}
				break;
			}
			int field;
			if(colSelect.equals("*") && colVisitor.isAggregate() == true) {
				field = 0;
			}
			else {
				field = whereRelation.getDesc().nameToId(colSelect);
			}
			if (!fieldList.contains(field))
				fieldList.add(field);
		}
		selectRelation = whereRelation.project(fieldList);

		// aggregate
		boolean flag = sb.getGroupByColumnReferences() != null;

		Relation aggregator;
		if(colVisitor.isAggregate() == true) {
			aggregator = selectRelation.aggregate(colVisitor.getOp(), flag);
		}
		else {
			aggregator = selectRelation;
		}

		return aggregator;

	}
}
