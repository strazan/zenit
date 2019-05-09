package main.java.zenit.javacodecompiler;

public class DebugError {
	
	private String place;
	private String problemType;
	private String problem;
	private int row;
	private int column;
	
	public DebugError(String place, String problemType, String problem, int row, int column) {
		this.place = place;
		this.problemType = problemType;
		this.problem = problem;
		this.row = row;
		this.column = column;
	}
	
	public String toString() {
		return "Place: " + place + "\nProblem type: " + problemType + "\nError: " + problem +
				"\n" + row + ":" + column;
	}

	public String getPlace() {
		return place;
	}

	public String getProblemType() {
		return problemType;
	}

	public String getProblem() {
		return problem;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	

}
