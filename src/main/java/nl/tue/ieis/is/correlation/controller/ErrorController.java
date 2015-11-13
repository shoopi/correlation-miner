package main.java.nl.tue.ieis.is.correlation.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Timer;

public class ErrorController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 7487882409009734575L;
	
	@Wire	private Grid errorGrid;
	@Wire	private Button clearBtn;
	@Wire	private Timer errorTimer;
	
	public static List<String> errors = new ArrayList<String>();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}
	
	@Listen("onClick=#clearBtn")
	public void clearErrors() {
		errorGrid.getRows().getChildren().clear();
		errors.clear();
	}
	
	@Listen("onTimer = #errorTimer")
	public void updateErrors() {
		if(errorGrid.getRows().getVisibleItemCount() != errors.size()) {
			errorGrid.getRows().getChildren().clear();
			for(String err : errors) {
				Row row = new Row();
				Label lbl = new Label(err);
				row.appendChild(lbl);
				errorGrid.getRows().appendChild(row);
			}
		}
	}
}
