package main.java.nl.tue.ieis.is.correlation.milp;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import main.java.nl.tue.ieis.is.correlation.AppRunner;
import main.java.nl.tue.ieis.is.correlation.logManager;
import main.java.nl.tue.ieis.is.correlation.config.ProjectConfig;
import main.java.nl.tue.ieis.is.correlation.graph.Edge;
import main.java.nl.tue.ieis.is.correlation.objects.ActivityTimeArray;
import main.java.nl.tue.ieis.is.correlation.objects.PossibleRelation;
import main.java.nl.tue.ieis.is.correlation.objects.VarBinaryRel;
import main.java.nl.tue.ieis.is.correlation.objects.VarUsage;

public class TraceCalculator {
	
	private List<PossibleRelation> allPossibleRelaions;
	private List<ActivityTimeArray> activityTimeList;
	private int allActualRelation;
	
	private Set<Edge> edges;
	private Map<String, VarBinaryRel> relToPossibleRel = new LinkedHashMap<String, VarBinaryRel>();
	
	public static double factor = 1;
	
	private PrintWriter writer; 
	
	public TraceCalculator(List<PossibleRelation> allPossibleRelaions, List<ActivityTimeArray> activityTimeList, Set<Edge> edges) {
		super();
		this.allPossibleRelaions = allPossibleRelaions;
		this.activityTimeList = activityTimeList;
		this.edges = edges;
		
		try {
			writer = new PrintWriter(ProjectConfig.desktopPath + "constraints.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public List<PossibleRelation> calculate() {
		List<PossibleRelation> finalRelations = new ArrayList<PossibleRelation>();
		try {
			GRBEnv    env   = new GRBEnv(ProjectConfig.desktopPath + "MilpLog2.log");	
			GRBModel  model = new GRBModel(env);
			
			System.out.println("\n\n\n******* Starting Trace Producer ******* \n");
			
			for(int i = 0; i < allPossibleRelaions.size(); i++) {
				VarUsage srcVar = new VarUsage(allPossibleRelaions.get(i).getSource(), UUID.randomUUID());
				VarUsage dstVar = new VarUsage(allPossibleRelaions.get(i).getDest(), UUID.randomUUID());
				VarBinaryRel binRel = new VarBinaryRel(srcVar, dstVar);

				model.addVar(0, 1, 0, GRB.BINARY, srcVar.toString());
				model.addVar(0, 1, 0, GRB.BINARY, dstVar.toString());
				model.addVar(0, 1, 0, GRB.BINARY, "rel_" + i);
				
				relToPossibleRel.put("rel_" + i, binRel);
				writer.println("[rel_" + i + "]" +  binRel.toString());
				
			}
			model.update();
			
			int constraintCounter = 1;
			
			writer.println("\n ============ USAGE CONSTRAINTS =========== \n");
			
			for(ActivityTimeArray at : activityTimeList) {
				for(DateTime dt : at.getTimestamps()) {
					GRBLinExpr usageSrcConstraint = new GRBLinExpr();
					GRBLinExpr usageDstConstraint = new GRBLinExpr();

					String variableUsageSrcStr = "[SRC][" + at.getActivity() + "_" + dt.toString() + "] --> "; 
					String variableUsageDstStr = "[DST][" + at.getActivity() + "_" + dt.toString() + "] --> "; 
					
					boolean moreSource = false;
					boolean moreDest = false;
					
					for(VarBinaryRel var : relToPossibleRel.values()) {
						if(var.getSourceVariable().getEvent().getActivity().contentEquals(at.getActivity()) &&
								var.getSourceVariable().getEvent().getTimestamp().equals(dt)) {
							usageSrcConstraint.addTerm(1, model.getVarByName(var.getSourceVariable().toString()));
							variableUsageSrcStr = variableUsageSrcStr + " + " + model.getVarByName(var.getSourceVariable().toString()).get(GRB.StringAttr.VarName);
							moreSource = true; 
						}
						if(var.getDestVaribale().getEvent().getActivity().contentEquals(at.getActivity()) &&
								var.getDestVaribale().getEvent().getTimestamp().equals(dt)) {
							usageDstConstraint.addTerm(1, model.getVarByName(var.getDestVaribale().toString()));
							variableUsageDstStr = variableUsageDstStr + " + " + model.getVarByName(var.getDestVaribale().toString()).get(GRB.StringAttr.VarName);
							moreDest = true;
						}
					}
					if(moreSource) {
						model.addConstr(usageSrcConstraint, GRB.EQUAL, 1.0, "UsageSrcConstraint_" + at.getActivity() + "_" + dt.toString()); 
						//System.out.println(constraintCounter + variableUsageSrcStr + " = 1");
						writer.println(constraintCounter + variableUsageSrcStr + " = 1 \n");
						constraintCounter++;
					}
					if(moreDest) {
						model.addConstr(usageDstConstraint, GRB.EQUAL, 1.0, "UsageDstConstraint_" + at.getActivity() + "_" + dt.toString()); 
						//System.out.println(constraintCounter + variableUsageDstStr + " = 1");
						writer.println(constraintCounter + variableUsageDstStr + " = 1 \n");
						constraintCounter++;
					}
					writer.println("\n-------- \n");
				}
			}
			
			DescriptiveStatistics stat = new DescriptiveStatistics();
			
			writer.println("\n ============ GRAPH CONSTRAINTS =========== \n");
			for(Edge e : edges) {
				allActualRelation = allActualRelation + (int) + e.getOccurance();
				
				String startNode = e.getSource().getActivityName();
				String endNode = e.getDest().getActivityName();
				
				GRBLinExpr graphConstraint = new GRBLinExpr();
				String graphUsageStr = "[ " + startNode + " -->" + endNode +  " ] "; 
				
				for(Map.Entry<String, VarBinaryRel> entry : relToPossibleRel.entrySet()) {
					if(entry.getValue().getSourceVariable().getEvent().getActivity().contentEquals(startNode) &&
							entry.getValue().getDestVaribale().getEvent().getActivity().contentEquals(endNode)){
						graphConstraint.addTerm(1, model.getVarByName(entry.getKey()));
						graphUsageStr = graphUsageStr + " + " + model.getVarByName(entry.getKey()).get(GRB.StringAttr.VarName);
						entry.getValue().setAverageDuration(e.getAverageDuration());
						entry.getValue().setEdgeOccurrance((int)(e.getOccurance()));
						entry.getValue().setStandardDeviation(e.getStandardDeviation());
					}
				}
				model.addConstr(graphConstraint, GRB.EQUAL, e.getOccurance(), "graphConstraint" + constraintCounter); 
				writer.println(constraintCounter + graphUsageStr + " = " + e.getOccurance() + " \n");
				constraintCounter++;
				for(int i = 0 ; i < e.getOccurance(); i++) {
					stat.addValue(e.getAverageDuration().getMillis());
				}
			}
			
			writer.println("\n ============ RELATION CONSTRAINTS =========== \n");
			GRBLinExpr relationConstraint = new GRBLinExpr();
			String relationUsageStr = "[RELATION] --> "; 
			for(String relVar : relToPossibleRel.keySet()) {
				relationConstraint.addTerm(1, model.getVarByName(relVar));
				relationUsageStr = relationUsageStr + " + " + model.getVarByName(relVar).get(GRB.StringAttr.VarName);
			}
			model.addConstr(relationConstraint, GRB.EQUAL, allActualRelation, "relationConstraint"); 
			writer.println(constraintCounter + relationUsageStr + " = " + allActualRelation + " \n");
			constraintCounter++;
			
			writer.println("\n ============ BINARY CONSTRAINTS =========== \n");
			for(Map.Entry<String, VarBinaryRel> entry : relToPossibleRel.entrySet()) {

				GRBLinExpr binaryConstraint1 = new GRBLinExpr();
				binaryConstraint1.addTerm(1, model.getVarByName(entry.getValue().getSourceVariable().toString()));
				binaryConstraint1.addTerm(-1, model.getVarByName(entry.getValue().getDestVaribale().toString()));
				model.addConstr(binaryConstraint1, GRB.EQUAL, 0.0, "binaryConstraint1_" + entry.getKey()); 
				String variableBinaryStr1 = model.getVarByName(entry.getValue().getSourceVariable().toString()).get(GRB.StringAttr.VarName)
						+ " = " + model.getVarByName(entry.getValue().getDestVaribale().toString()).get(GRB.StringAttr.VarName);
				writer.println(constraintCounter + variableBinaryStr1 + " \n");
				constraintCounter++;
				
				GRBLinExpr binaryConstraint2 = new GRBLinExpr();
				binaryConstraint2.addTerm(1, model.getVarByName(entry.getValue().getSourceVariable().toString()));
				binaryConstraint2.addTerm(-1, model.getVarByName(entry.getKey()));
				model.addConstr(binaryConstraint2, GRB.EQUAL, 0.0, "binaryConstraint2_" + entry.getKey()); 
				String variableBinaryStr2 = model.getVarByName(entry.getValue().getSourceVariable().toString()).get(GRB.StringAttr.VarName)
						+ " = " + model.getVarByName(entry.getKey()).get(GRB.StringAttr.VarName);
				writer.println(constraintCounter + variableBinaryStr2 + " \n");
				constraintCounter++;
				
				//add min and max to each variable
				long currentDiff = (entry.getValue().getDestVaribale().getEvent().getTimestamp().getMillis() - entry.getValue().getSourceVariable().getEvent().getTimestamp().getMillis());
				if( currentDiff < entry.getValue().getMinDuration()){
					entry.getValue().setMinDuration(currentDiff);
				}
				if(currentDiff > entry.getValue().getMaxDuration()) {
					entry.getValue().setMaxDuration(currentDiff);
				}
			}
			
			GRBQuadExpr objectiveExpression = new GRBQuadExpr();
						
			String objectiveStr = "[OBJECTIVE] " ;
			for(Map.Entry<String, VarBinaryRel> entry : relToPossibleRel.entrySet()) {
				VarUsage source = entry.getValue().getSourceVariable();
				VarUsage dest = entry.getValue().getDestVaribale();
				
				GRBVar varSource = model.getVarByName(source.toString());
				GRBVar varDest = model.getVarByName(dest.toString());
				GRBVar rel = model.getVarByName(entry.getKey());
				
				double timeDiff = dest.getEvent().getTimestamp().getMillis() - source.getEvent().getTimestamp().getMillis();
				double average = entry.getValue().getAverageDuration().getMillis();
				double min = entry.getValue().getMinDuration();
				double max = entry.getValue().getMaxDuration();
				double constant = //(1.0/20.0) * 
						//(double)dest.getEvent().getTimestamp().getMillis()/(double)entry.getValue().getAverageDuration().getMillis();
						//(double)entry.getValue().getAverageDuration().getMillis() / (double)stat.getMean();
						//(double)entry.getValue().getEdgeOccurrance() / 20.0;
						//stat.getMean() * (double)(timeDiff / (entry.getValue().getAverageDuration().getMillis()));
						//entry.getValue().getMinDuration() / (entry.getValue().getMaxDuration() - entry.getValue().getMinDuration());
						//(entry.getValue().getMaxDuration());
						//timeDiff;
						Math.sqrt(timeDiff - (double)((max - min)/2)) * factor;
						
				
				System.out.println("CONSTANT " + constant + " -- MEAN: " + stat.getMean());
				writer.println("CONSTANT " + constant + " -- MEAN: " + stat.getMean());
				
				double coefficientSource = 	constant * (source.getEvent().getTimestamp().getMillis()) * (-1);
				double coefficientDest = 	constant * dest.getEvent().getTimestamp().getMillis();
				objectiveExpression.addTerm(coefficientDest, rel, varDest);
				objectiveExpression.addTerm(coefficientSource, rel, varSource);
				
				objectiveStr = objectiveStr + coefficientDest + " X " +  rel.get(GRB.StringAttr.VarName) + " X " +
						varDest.get(GRB.StringAttr.VarName) + " X " +  coefficientSource + " X " + 
						" X " +  rel.get(GRB.StringAttr.VarName) + " X " + varSource.get(GRB.StringAttr.VarName);

				
			}
			System.out.println("\n" + objectiveStr);
			writer.println("\n" + objectiveStr);
			
			model.setObjective(objectiveExpression, GRB.MINIMIZE);
			model.optimize();
			
			int optimstatus = model.get(GRB.IntAttr.Status);
			double objval = 0;
			
			DescriptiveStatistics stat2 = new DescriptiveStatistics();
			DescriptiveStatistics stat3 = new DescriptiveStatistics();

			
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
				System.out.println("Optimization was stopped with status = " + optimstatus);	
			}
			
			logManager lm = new logManager();

			System.err.println("number of solutions: " + model.get(GRB.IntAttr.SolCount));
			int solCounter = model.get(GRB.IntAttr.SolCount);
			
			
			
			while(solCounter > 0) {
				finalRelations = new ArrayList<PossibleRelation>();
				env.set(GRB.IntParam.SolutionNumber, solCounter);
				int varCounter = 0;
				int finalRelCounter = 1;
				System.out.println("\n\n============= FINAL RELATION ============ \n\n");
				writer.println("\n\n============= FINAL RELATION ============ \n\n");
				System.out.println("SOLUTION " + env.get(GRB.IntParam.SolutionNumber) + "\n");
				writer.println("SOLUTION " + env.get(GRB.IntParam.SolutionNumber) + "\n");
				for(GRBVar var : model.getVars()) { 
					if(var.get(GRB.DoubleAttr.Xn) > 0) {
						varCounter++;
						String varName = var.get(GRB.StringAttr.VarName);
						writer.println(varCounter + ". " + varName + ": " + var.get(GRB.DoubleAttr.Xn));
						if(varName.contains("rel_")) {
							finalRelations.add(new PossibleRelation(relToPossibleRel.get(varName).getSourceVariable().getEvent(), 
									relToPossibleRel.get(varName).getDestVaribale().getEvent()));
							stat2.addValue(relToPossibleRel.get(varName).getAverageDuration().getMillis());
							System.out.println("[" + finalRelCounter + "] (" + varName + ")" + relToPossibleRel.get(varName).toString());
							Duration d = new Duration(relToPossibleRel.get(varName).getSourceVariable().getEvent().getTimestamp(),
									relToPossibleRel.get(varName).getDestVaribale().getEvent().getTimestamp());
							System.out.println("DURATION: " + relToPossibleRel.get(varName).getAverageDuration().getMillis() 
									+ " -- " + d.getMillis() + "\n");
							stat3.addValue(d.getMillis());
							writer.println("[" + finalRelCounter + "] (" + varName + ")" +  relToPossibleRel.get(varName).toString() + "\n");
							finalRelCounter++;
						}
					}
				}
				System.out.println("Objective Value: " + model.get(GRB.DoubleAttr.ObjVal));
				writer.println("Objective Value: " + model.get(GRB.DoubleAttr.ObjVal));
				lm.doTraceEvaluationAppRunner(new HashSet<PossibleRelation>(finalRelations), AppRunner.filename, AppRunner.username, AppRunner.logProducer);
				solCounter--;	
			}
			model.dispose();
			env.dispose();
			writer.close();
			
			System.out.println("\n=======\n MINED AVERAGE " + stat.getMean() + "==============\n");
			System.out.println("\n=======\n TRACE AVERAGE " + stat2.getMean() + " -- "  + stat3.getMean() + "==============\n");
		
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
			writer.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
			writer.close();
		}
		
		return finalRelations;	
	}
}