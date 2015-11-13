package main.java.nl.tue.ieis.is.correlation.duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import gurobi.*;

public class MILPTimeMapping {
	
	private List<DateTime> sourceList;
	private List<DateTime> targetList;
	
	private List<DateTime> resultSource;
	private List<DateTime> resultTarget;
	public MILPTimeMapping(List<DateTime> sourceList, List<DateTime> targetList) {
		super();
		this.sourceList = sourceList;
		this.targetList = targetList;
	}



	public Map<DateTime, DateTime> calculate() {
		Map<DateTime, DateTime> finalMapping = new HashMap<DateTime, DateTime>();
		try {
			GRBEnv    env   = new GRBEnv("mip1.log");			
			GRBModel  model = new GRBModel(env);
			
			for(int i = 0; i < sourceList.size(); i++) {
				
				model.addVar(0, 1, 0, GRB.INTEGER, "s" + i);
			}
			for(int i = 0; i < targetList.size(); i++) {	
				model.addVar(0, 1, 0, GRB.INTEGER, "t" + i);
			}
			
			model.update();
			
			int minOcc = Math.min(sourceList.size(), targetList.size());
			GRBLinExpr objectiveExpression = new GRBLinExpr();
			int possibilities = 0;
			
			for(int i = 0; i < sourceList.size(); i++) {
				for (int j = 0; j<targetList.size(); j++) {
					if(!targetList.get(j).isBefore(sourceList.get(i))){

						possibilities = possibilities + 1;
						objectiveExpression.addTerm(targetList.get(j).getMillis(), model.getVarByName("t" + j));
						objectiveExpression.addTerm((-1) * sourceList.get(i).getMillis(), model.getVarByName("s" + i));
						
						/*
						long value = targetList.get(j).getMillis() - sourceList.get(i).getMillis();
						int coeff = model.getVarByName("t" + j);
						*/
					}
				}
			}
			
			if(possibilities < minOcc) minOcc = possibilities;
			
			if(minOcc == 0) return null;
			
			GRBLinExpr sourceListExpression = new GRBLinExpr();
			GRBLinExpr targetListExpression = new GRBLinExpr();

				
			for(int i = 0; i < sourceList.size(); i++) {
				sourceListExpression.addTerm(1, model.getVarByName("s" + i));
			}
			for(int i = 0; i < targetList.size(); i++) {	
				targetListExpression.addTerm(1, model.getVarByName("t" + i));
			}
			
			model.addConstr(sourceListExpression, GRB.EQUAL, minOcc, "SourceListConstraint");			
			model.addConstr(targetListExpression, GRB.EQUAL, minOcc, "TargetListConstraint");
			//model.addConstr(objectiveExpression, GRB.GREATER_EQUAL, 0, "ObjectiveConstraint");
			
			
			model.setObjective(objectiveExpression, GRB.MINIMIZE);
	      // Optimize model
	      model.optimize();
	     	      
	      int optimstatus = model.get(GRB.IntAttr.Status);
	      double objval = 0;
	      if (optimstatus == GRB.Status.OPTIMAL) {
	        objval = model.get(GRB.DoubleAttr.ObjVal);
	        System.out.println("Optimal objective: " + objval);
	      } else if (optimstatus == GRB.Status.INF_OR_UNBD) {
	        System.out.println("Model is infeasible or unbounded");
	      } else if (optimstatus == GRB.Status.INFEASIBLE) {
	        System.out.println("Model is infeasible");
	      } else if (optimstatus == GRB.Status.UNBOUNDED) {
	        System.out.println("Model is unbounded");
	      } else {
	        System.out.println("Optimization was stopped with status = "
	            + optimstatus);
	      }

	      System.err.println("number of solutions: " + model.get(GRB.IntAttr.SolCount));
	      
	      int solCounter = model.get(GRB.IntAttr.SolCount);
	      //while
    	  List<String> sources = new ArrayList<String>();
    	  List<String> targets = new ArrayList<String>();
	      if(solCounter > 0) {
	    	 // env.set(GRB.IntParam.SolutionNumber, solCounter - 1);
	    	  System.out.println("SOLUTION " + env.get(GRB.IntParam.SolutionNumber));
	    	  //GRBLinExpr eliminationConstraint = new GRBLinExpr();
	    	  int variableCounter = 1;

	    	  for(GRBVar var : model.getVars()) {
		    	  if(var.get(GRB.DoubleAttr.Xn) > 0) {
		    		  String varName = var.get(GRB.StringAttr.VarName);
	    			  System.out.println(varName);
	    			  if(varName.contains("s"))
	    				  sources.add(varName);
	    			  else
	    				  targets.add(varName);
	    			  
	    			  }
		    	  } 
	      } else {
		    		 // System.out.println(var.get(GRB.StringAttr.VarName) + "\nOccurance: " + var.get(GRB.DoubleAttr.X) + "\n");
		    	  }
	      Collections.sort(sources);
		  Collections.sort(targets);
		  
		  resultSource = new ArrayList<DateTime>();
		  resultTarget = new ArrayList<DateTime>();
		  
		  for(int i = 0 ; i < sources.size(); i++) {
			  DateTime first = sourceList.get(Integer.valueOf(sources.get(i).substring(1)));
			  DateTime second = targetList.get(Integer.valueOf(targets.get(i).substring(1)));
			  
			  resultSource.add(first);
			  resultTarget.add(second);
			  
			  finalMapping.put(first, second);
	    	  //model.optimize();
	    	  solCounter--;
	      }
    	  System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
    	  System.out.println("=========================\n\n");
	      model.dispose();
	      env.dispose();

	    } catch (GRBException e) {
	      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
	    }
	  return finalMapping;
  }



	public List<DateTime> getResultSource() {
		return resultSource;
	}

	public List<DateTime> getResultTarget() {
		return resultTarget;
	}

}