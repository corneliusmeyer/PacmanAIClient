package de.fh.stud.pacmanFinal;

import java.util.LinkedList;
import java.util.List;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;

public class Knoten {
	private Vector2 pos;
	private static PacmanTileType[][] view;
	private static double[][] ghostInfo;
	private static PacmanPercept percept;
	PacmanAction action;
	Knoten parent;
		
	public Knoten(Vector2 pos, PacmanAction action, Knoten parent) {
		this.pos = pos;
		this.action = action;
		this.parent = parent;
	}


	Knoten(PacmanTileType[][] view, PacmanPercept percept, double[][] ghostInfo,Vector2 pos) {
		this.pos = pos;
		Knoten.view = view;
		Knoten.ghostInfo = ghostInfo;
		Knoten.percept = percept;
	}
	
	Knoten(PacmanTileType[][] view, Vector2 pos) {
		this.pos = pos;
		Knoten.view = view;
	}
	
	
	public Vector2 getPosition() {
		return pos;
	}
	
	public Knoten getParent() {
		return parent;
	}
	
	public PacmanAction getAction() {
		return action;
	}
	
	public List<Knoten> expand() {
		LinkedList<Knoten> children  = new LinkedList<Knoten>();
		Vector2 pruefPos;
		
		//Norden
		pruefPos = new Vector2(pos.getX(), pos.getY()-1);
		if(darfExpandenNach(pruefPos)) {
			children.add(new Knoten(pruefPos, PacmanAction.GO_NORTH, this));
		}
		//Osten
		pruefPos = new Vector2(pos.getX()+1, pos.getY());
		if(darfExpandenNach(pruefPos)) {
			children.add(new Knoten(pruefPos, PacmanAction.GO_EAST, this));
		}
		//Süden
		pruefPos = new Vector2(pos.getX(), pos.getY()+1);
		if(darfExpandenNach(pruefPos)) {
			children.add(new Knoten(pruefPos, PacmanAction.GO_SOUTH, this));
		}
		//Westen
		pruefPos = new Vector2(pos.getX()-1, pos.getY());
		if(darfExpandenNach(pruefPos)) {
			children.add(new Knoten(pruefPos, PacmanAction.GO_WEST, this));
		}
		return children;
	}
	
	public boolean darfExpandenNach(Vector2 pos) {
		if(ghostInfo != null) {
			if(ghostInfo[pos.x][pos.y] < -0.7)
				return false;
		}
		return (view[pos.x][pos.y] == PacmanTileType.DOT 
				|| view[pos.x][pos.y] == PacmanTileType.EMPTY
				|| view[pos.x][pos.y] == PacmanTileType.POWERPILL || (Helper.istGhostAufPosition(view, pos) && percept != null && Helper.getPowerPillTimer(percept) > 1));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Knoten other = (Knoten) obj;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}
	
	
}
