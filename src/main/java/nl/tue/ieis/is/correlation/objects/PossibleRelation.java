package main.java.nl.tue.ieis.is.correlation.objects;


public class PossibleRelation {
	private SimpleEventObject source;
	private SimpleEventObject dest;
	public SimpleEventObject getSource() {
		return source;
	}
	public void setSource(SimpleEventObject source) {
		this.source = source;
	}
	public SimpleEventObject getDest() {
		return dest;
	}
	public void setDest(SimpleEventObject dest) {
		this.dest = dest;
	}
	public PossibleRelation(SimpleEventObject source, SimpleEventObject dest) {
		super();
		this.source = source;
		this.dest = dest;
	}
	
	public boolean equals(Object o) {
		return ((o instanceof PossibleRelation) && 
				((((PossibleRelation) o).source).equals(this.source)) && 
				((((PossibleRelation) o).dest).equals(this.dest)));
	}
	
	public int hashCode() {
        return source.hashCode() ^ dest.hashCode();
    }
	
	public String toString(){
		return source.toString() + " ==> " + dest.toString();
	}

}
