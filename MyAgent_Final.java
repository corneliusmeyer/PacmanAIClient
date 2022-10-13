package de.fh.stud.pacmanFinal;

import de.fh.kiServer.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.fh.kiServer.agents.Agent;
import de.fh.kiServer.util.Util;
import de.fh.kiServer.util.Vector2;
import de.fh.pacman.GhostInfo;
import de.fh.pacman.PacmanAgent_2021;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.GhostType;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.pacman.enums.PacmanTileType;

public class MyAgent_Final extends PacmanAgent_2021 {

	@Override
	public PacmanAction action(PacmanPercept percept, PacmanActionEffect actionEffect) {
		
		
		/*****  Global Vars  ******/
		PacmanTileType[][] feld = percept.getView();
		Vector2 myPos = percept.getPosition();
		ArrayList<Vector2> dots = Helper.berechneAlleDots(feld);
		/*************************/
		
		double[][] relevanz = new double[feld.length][feld[0].length];
		
		
		double[][] relevanzFeld = Bewertungen.bewerteFeldTileType(feld, Helper.getPowerPillTimer(percept) > 2 ? -1 : 1);
		double[][] relevanzGeisterDist = Bewertungen.bewerteGeisterDistanz(percept, feld);
		double[][] relevanzDotDist = Bewertungen.bewerteDotDistanz(dots, feld);
		double[][] relevanzDistDotPacman = Bewertungen.bewerteDistanzDotZuPacman(dots, myPos, feld);
		//double[][] relevanzSackgasse =  new double[feld.length][feld[0].length]; 
		double[][] relevanzSackgasse = Bewertungen.bewerteSackgassen(feld, percept, dots.size());
	    
		for(int x = 0; x < feld.length; x++) {
			for(int y = 0; y < feld[0].length; y++) {
				relevanz[x][y] = relevanzFeld[x][y] + relevanzGeisterDist[x][y] + relevanzDotDist[x][y]
						+ relevanzDotDist[x][y] + relevanzDistDotPacman[x][y] + relevanzSackgasse[x][y];
			}
		}
		
		Vector2 besterSpot = Helper.berechneBestenSpot(relevanz);
		/*System.out.println("Meine Pos: " + myPos + "  Der beste Spot ist " + besterSpot);
		System.out.println("Berechnung: " + relevanz[besterSpot.getX()][besterSpot.getY()] + "{F: " + relevanzFeld[besterSpot.getX()][besterSpot.getY()] + 
				" G: " + relevanzGeisterDist[besterSpot.getX()][besterSpot.getY()] + " D: " + relevanzDistDotPacman[besterSpot.getX()][besterSpot.getY()] +
				" S: " + relevanzSackgasse[besterSpot.getX()][besterSpot.getY()] + "}");*/
		Breitensuche suche;
		if(Bewertungen.ignoreGhost) {
			suche = new Breitensuche(feld, myPos, besterSpot);
		}
		else suche = new Breitensuche(feld, percept, relevanzGeisterDist, myPos, besterSpot); 
		//Vorsichtige Breitensuche zu bestem Punkt
		Knoten weg = suche.run();
		if(weg != null) {
			Knoten naechster = weg;
			while(naechster.getParent() != null  && naechster.getParent().getParent() != null) 
				naechster = naechster.getParent();
			if(naechster.getAction() != null) return naechster.getAction();
		}
		//Breitensuche zu bestem Punkt
		suche = new Breitensuche(feld, myPos, besterSpot);
		weg = suche.run();
		if(weg != null) {
			Knoten naechster = weg;
			while(naechster.getParent() != null  && naechster.getParent().getParent() != null) 
				naechster = naechster.getParent();
			if(naechster.getAction() != null) return naechster.getAction();
		}
		//Breitensuche zu irgendeinen Punkt
		suche = new Breitensuche(feld, myPos, Helper.berechneSpotWeitWegVonGhosts(percept));
		weg = suche.run();
		if(weg != null) {
			Knoten naechster = weg;
			while(naechster.getParent() != null  && naechster.getParent().getParent() != null) 
				naechster = naechster.getParent();
			if(naechster.getAction() != null) return naechster.getAction();
		}
		//Lokale Suche
		int richtung = 0;
		double compare = relevanz[myPos.getX()][myPos.getY()-1];
		double current = relevanz[myPos.getX()+1][myPos.getY()];
		if(current > compare) {
			richtung = 1;
			compare = current;
		}
		current = relevanz[myPos.getX()][myPos.getY()+1];
		if(current > compare) {
			richtung = 2;
			compare = current;
		}
		current = relevanz[myPos.getX()-1][myPos.getY()];
		if(current > compare) {
			richtung = 3;
			compare = current;
		}
		if(compare < relevanz[myPos.getX()][myPos.getY()]) {
			//Wenn die Position von Pacman besser ist als die Felder drum herum
			return PacmanAction.WAIT;
		}
		while(Helper.istGhostAufPosition(feld, Helper.getPunktNebenPosition(myPos, richtung))) richtung++;
		switch(richtung) {
			case 0: return PacmanAction.GO_NORTH;
			case 1: return PacmanAction.GO_EAST;
			case 2: return PacmanAction.GO_SOUTH;
			case 3: return PacmanAction.GO_WEST;
		}
		return PacmanAction.WAIT;
	}
	

	
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void onGameStart(PacmanStartInfo startInfo) {
		
	}

	@Override
	protected void onGameover(PacmanGameResult gameResult) {
		//ein insider :D
		/*if(gameResult.getRemainingDots() == 0) {
			System.out.println("Danke dir Digga!");
		} else System.out.println("Bitte dir Digga!");*/
	}
	
	public MyAgent_Final(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		MyAgent_Final agent = new MyAgent_Final("MyAgent");
		Agent.start(agent, "127.0.0.1", 5000);
	}
	
}
