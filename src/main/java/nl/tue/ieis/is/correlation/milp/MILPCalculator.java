package main.java.nl.tue.ieis.is.correlation.milp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gurobi.*;
import main.java.nl.tue.ieis.is.correlation.objects.*;

public class MILPCalculator {
	private List<PossibleEdge> possibleEdges;
	private Map<String, Integer> incoming;
	private Map<String, Integer> outgoing;
	private boolean allEdge = false;

	public void setAllEdge(boolean allEdge) {
		this.allEdge = allEdge;
	}

	public List<PossibleEdge> getPossibleEdges() {
		return possibleEdges;
	}

	public void setPossibleEdges(List<PossibleEdge> possibleEdges) {
		this.possibleEdges = possibleEdges;
	}

	public MILPCalculator(List<PossibleEdge> possibleEdges, Map<String, Integer> incoming, Map<String, Integer> outgoing) {
		this.possibleEdges = possibleEdges;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}
	
	public Map<PossibleEdge, Integer> calculate() {
		Map<PossibleEdge, Integer> finalEdgeList = new HashMap<PossibleEdge, Integer>();
		try {
			GRBEnv    env   = new GRBEnv("MilpLog.log");	
			GRBModel  model = new GRBModel(env);
			
			for(PossibleEdge e : possibleEdges) {
				int upperBound = Math.min(incoming.get(e.getActivity2()), outgoing.get(e.getActivity1()));
				model.addVar(0, upperBound, 0, GRB.INTEGER, e.toString());
			}
			model.update();
			
			if(allEdge) {
				for(PossibleEdge e : possibleEdges) {
					GRBLinExpr tempConstraint = new GRBLinExpr();
					tempConstraint.addTerm(1, model.getVarByName(e.toString()));
					model.addConstr(tempConstraint, GRB.GREATER_EQUAL, 1.0, tempConstraint.toString()); 
				}
			}

			for(Map.Entry<String, Integer> node : incoming.entrySet()) {
				GRBLinExpr incomingExp = new GRBLinExpr();
				GRBLinExpr outgoingExp = new GRBLinExpr();
				
				String incomingExpConstStr = "[incoming] ";
				String outgoingExpConstStr = "[outgoing] ";
				
				for(PossibleEdge e : possibleEdges) {
					if(e.getActivity1().contentEquals(node.getKey())) {
						outgoingExp.addTerm(1, model.getVarByName(e.toString()));
						String o = model.getVarByName(e.toString()).get(GRB.StringAttr.VarName);
						if(o.contains("Std Deviation")) {
							outgoingExpConstStr = outgoingExpConstStr + o.substring(0, o.indexOf("Std Deviation") - 1) + " + ";
						} else {
							outgoingExpConstStr = outgoingExpConstStr + o.substring(0, o.indexOf("Duration") - 1) + " + ";
						}
					}
					else if(e.getActivity2().contentEquals(node.getKey())) {
						incomingExp.addTerm(1, model.getVarByName(e.toString()));
						String i = model.getVarByName(e.toString()).get(GRB.StringAttr.VarName);
						if(i.contains("Std Deviation")) {
							incomingExpConstStr = incomingExpConstStr + i.substring(0, i.indexOf("Std Deviation") - 1) + " + ";
						} else {
							incomingExpConstStr = incomingExpConstStr + i.substring(0, i.indexOf("Duration") - 1) + " + ";
						}
					}
				}
				
				incomingExpConstStr = incomingExpConstStr + " = " + incoming.get(node.getKey());
				outgoingExpConstStr = outgoingExpConstStr + " = " + outgoing.get(node.getKey());
				
				model.addConstr(incomingExp, GRB.EQUAL, incoming.get(node.getKey()), node.getKey() + "_incoming");
				model.addConstr(outgoingExp, GRB.EQUAL, outgoing.get(node.getKey()), node.getKey() + "_outgoing");
				
				GRBLinExpr objectiveExpression = new GRBLinExpr();
				for(PossibleEdge e : possibleEdges) {
					GRBVar var = model.getVarByName(e.toString());
					int occuranceA = Math.max(incoming.get(e.getActivity1()), outgoing.get(e.getActivity1()));
					int occuranceB = Math.max(incoming.get(e.getActivity2()), outgoing.get(e.getActivity2()));
					
					double ratio = 0.0;
					if(e.getDuration() != null)
						ratio = (double)((double)(e.getDuration().getMillis()) / (double)e.getProbability());
					else
						ratio = (double)(e.getStdDevDiff() / (double)e.getProbability());
					
					double coefficient = (double) (ratio / (double)Math.min(occuranceA, occuranceB));
					objectiveExpression.addTerm(coefficient, var);
				}
				model.setObjective(objectiveExpression, GRB.MINIMIZE);
			}
			
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
	      if(solCounter > 0) {
	    	  System.out.println("SOLUTION " + env.get(GRB.IntParam.SolutionNumber));
	    	  int variableCounter = 1;
	    	  for(GRBVar var : model.getVars()) {
		    	  if(var.get(GRB.DoubleAttr.Xn) > 0) {
		    		  String varName = var.get(GRB.StringAttr.VarName);
	    			  String activity1 = varName.substring(varName.indexOf("(") + 1, varName.indexOf(" --> "));
	    			  String activity2 = varName.substring(varName.indexOf(" --> ") + 5, varName.indexOf(")"));
	    			  
		    		  for(PossibleEdge p : possibleEdges) { 
		    			  if(p.getActivity1().contentEquals(activity1) && p.getActivity2().contentEquals(activity2)) {
		    				  System.out.println(variableCounter + ". " + activity1 + " --> "  + activity2 + ": (" + (int)var.get(GRB.DoubleAttr.Xn) + ")");
		    	    		  finalEdgeList.put(p, (int)var.get(GRB.DoubleAttr.Xn));
		    	    		  variableCounter++;
		    			  }
		    		  }
		    		  
		    	  } else {}
		      }
	    	  System.out.println("Objective Value: " + model.get(GRB.DoubleAttr.ObjVal));
	    	  System.out.println("======================================");
	    	  solCounter--;
	      }
	      
	      
	      model.dispose();
	      env.dispose();

	    } catch (GRBException e) {
	      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
	    }
	  return finalEdgeList;
  }
}