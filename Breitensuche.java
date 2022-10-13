package de.fh.stud.pacmanFinal;

import java.util.LinkedList;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanTileType;


public class Breitensuche {
	private boolean[][] closedList; 
	private LinkedList<Knoten> openList;
	private PacmanTileType[][] feld;
	private Vector2 start;
	private Vector2 ziel;
	private double[][] ghostInfos;
	private PacmanPercept percept;
	
	public Knoten run() {
		closedList = new boolean[feld.length][feld[0].length];
		openList.clear();
		Knoten anfang;
		if(ghostInfos == null)
			anfang = new Knoten(feld, start);
		else  anfang = new Knoten(feld, percept, ghostInfos, start);
		openList.add(anfang);
		while(!openList.isEmpty()) {
			Knoten tmp = openList.remove(0);
			if(tmp.getPosition().equals(ziel)) { return tmp; }
			if(!closedList[tmp.getPosition().x][tmp.getPosition().y]) {
				closedList[tmp.getPosition().x][tmp.getPosition().y] = true;
				openList.addAll(tmp.expand());
			}
		}
		return null;
		
	}
	
	Breitensuche(PacmanTileType[][] feld, Vector2 start, Vector2 ziel) {
		this.feld = feld;
		this.start = start;
		this.ziel = ziel;
		openList = new LinkedList<Knoten>();
	}
	
	Breitensuche(PacmanTileType[][] feld, PacmanPercept percept, double[][] ghostInfos, Vector2 start, Vector2 ziel) {
		this.feld = feld;
		this.start = start;
		this.ziel = ziel;
		this.ghostInfos = ghostInfos;
		this.percept = percept;
		openList = new LinkedList<Knoten>();
	}
}