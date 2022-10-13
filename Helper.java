package de.fh.stud.pacmanFinal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import de.fh.kiServer.util.Util;
import de.fh.kiServer.util.Vector2;
import de.fh.pacman.GhostInfo;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.GhostType;
import de.fh.pacman.enums.PacmanTileType;
import javafx.geometry.Pos;

public class Helper {
	static boolean istGhostAufPosition(PacmanTileType[][] feld, Vector2 pos) {
		return feld[pos.x][pos.y] == PacmanTileType.GHOST 
				|| feld[pos.x][pos.y] == PacmanTileType.GHOST_AND_DOT
				|| feld[pos.x][pos.y] == PacmanTileType.GHOST_AND_POWERPILL;
	}
	
	
	static boolean istGhostAufPosition(PacmanTileType tile) {
		return tile == PacmanTileType.GHOST 
				|| tile == PacmanTileType.GHOST_AND_DOT
				|| tile == PacmanTileType.GHOST_AND_POWERPILL;
	}
	
	//Percept == nulll
	
	static boolean istNebenGeist(PacmanTileType[][] view, Vector2 pos) {
		if(istGhostAufPosition(view, new Vector2(pos.x, pos.y-1))) return true;
		else if(istGhostAufPosition(view, new Vector2(pos.x+1, pos.y))) return true;
		else if(istGhostAufPosition(view, new Vector2(pos.x, pos.y+1))) return true;
		else if(istGhostAufPosition(view, new Vector2(pos.x-1, pos.y))) return true;
		else return false;
	}
	
	static int getPowerPillTimer(PacmanPercept percept) {
		if(!percept.getGhostInfos().isEmpty()) {
			return percept.getGhostInfos().get(0).getPillTimer();
		}
		return -1;
	}

	
	
	static ArrayList<Vector2> berechneAlleDots(PacmanTileType[][] map) {
		ArrayList<Vector2> dots = new ArrayList<Vector2>();
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {
				if(map[x][y] == PacmanTileType.DOT)
					dots.add(new Vector2(x, y));
			}
		}
		return dots;
	}
	
	static int getDistanzZuHunter(PacmanPercept percept, Vector2 pos) {
		int dist = 999999;
		for(GhostInfo ghost : percept.getGhostInfos()) {
			if(ghost.getType().equals(GhostType.GHOST_HUNTER.toString())) {
				int tmp = Util.manhattan(pos.x, pos.y, ghost.getPos().x, ghost.getPos().y);
				if(tmp < dist) dist = tmp; 
			}
		}
		return dist;
	}
	
	
	
	static Vector2 berechneBestenSpot(double feld[][]) {
		Vector2 bester = new Vector2(0,0);
		double wert = 0.0;
		for(int x = 0; x < feld.length; x++) {
			for(int y = 0; y < feld[0].length; y++) {
				if(feld[x][y] > wert) {
					bester = new Vector2(x, y);
					wert = feld[x][y];
				}
			}
		}
		return bester;
	}
	
	static Vector2 berechneSpotWeitWegVonGhosts(PacmanPercept percept) {
		Vector2 pos = new Vector2(1, 1);
		PacmanTileType[][] feld = percept.getView();
		int dist = 0;
		for(int x = 0; x < feld.length; x++) {
			for(int y = 0; y < feld[0].length; y++) {
				if(Helper.istGhostAufPosition(feld[x][y]) || feld[x][y] == PacmanTileType.WALL) continue; 
				int sum = 0;
				for(GhostInfo ghost : percept.getGhostInfos()) {
					sum += Util.manhattan(x, y, ghost.getPos().x, ghost.getPos().y);
				}
				if(dist > sum) {
					dist = sum;
					pos = new Vector2(x, y);
				}
			}
		}
		return pos;
	}
	
	static Vector2 getPunktNebenPosition(Vector2 pos, int richtung) {
		Vector2 result = new Vector2(0, 0);
		switch(richtung) {
			case 0: {
				result = new Vector2(pos.x, pos.y-1);
				break;
			}
			case 1: {
				result = new Vector2(pos.x+1, pos.y);
				break;
			}
			case 2: {
				result = new Vector2(pos.x, pos.y+1);
				break;
			}
			case 3: {
				result = new Vector2(pos.x-1, pos.y);
				break;
			}
		}
		return result;
	}
	
	
	static ArrayList<Sackgasse> berechneSackgassen(PacmanTileType[][] map) {
		ArrayList<Sackgasse> sackgassen = new ArrayList<Sackgasse>();
		for(int x = 1; x < map.length-1; x++) {
			for(int y = 1; y < map[0].length-1; y++) {
				boolean[] flag = new boolean[4];
				int besetzteSeiten = 0;
				if(map[x][y] == PacmanTileType.WALL || Helper.istGhostAufPosition(map, new Vector2(x, y))) continue;
				if(map[x][y-1] == PacmanTileType.WALL || Helper.istGhostAufPosition(map, new Vector2(x, y-1))) flag[0] = true;
				if(map[x+1][y] == PacmanTileType.WALL || Helper.istGhostAufPosition(map, new Vector2(x+1, y))) flag[1] = true;
				if(map[x][y+1] == PacmanTileType.WALL || Helper.istGhostAufPosition(map, new Vector2(x, y+1))) flag[2] = true;
				if(map[x-1][y] == PacmanTileType.WALL || Helper.istGhostAufPosition(map, new Vector2(x-1, y))) flag[3] = true;
				for(int i = 0; i < flag.length; i++) {
					if(flag[i]) besetzteSeiten++;
				}
				if(besetzteSeiten == 3) {
					Sackgasse sackgasse = new Sackgasse();
					Vector2 pruef = null;
					Vector2 last = new Vector2(x, y);
					//Pfad entlang gehen
					while(besetzteSeiten == 3) {
						int prevRichtung = -1;
						if(!flag[0]) {
							prevRichtung = 2;
							pruef = new Vector2(last.getX(), last.getY()-1);
						}
						else if(!flag[1]) {
							prevRichtung = 3;
							pruef = new Vector2(last.getX()+1, last.getY());
						}
						else if(!flag[2]) {
							prevRichtung = 0;
							pruef = new Vector2(last.getX(), last.getY()+1);
						}
						else if(!flag[3]) {
							prevRichtung = 1;
							pruef = new Vector2(last.getX()-1, last.getY());
						}
						sackgasse.wegHinzufuegen(new Sackgassenpfad(last, prevRichtung, map[last.x][last.y]));
						
						besetzteSeiten = 0; //wird später erhöht
						flag = new boolean[4];
						Vector2 neuerPruef;
						neuerPruef = new Vector2(pruef.getX(), pruef.getY()-1);
						if(last.equals(neuerPruef) || map[neuerPruef.getX()][neuerPruef.getY()] == PacmanTileType.WALL) flag[0] = true;
						neuerPruef = new Vector2(pruef.getX()+1, pruef.getY());
						if(last.equals(neuerPruef) || map[neuerPruef.getX()][neuerPruef.getY()] == PacmanTileType.WALL) flag[1] = true;
						neuerPruef = new Vector2(pruef.getX(), pruef.getY()+1);
						if(last.equals(neuerPruef) || map[neuerPruef.getX()][neuerPruef.getY()] == PacmanTileType.WALL) flag[2] = true;
						neuerPruef = new Vector2(pruef.getX()-1, pruef.getY());
						if(last.equals(neuerPruef) || map[neuerPruef.getX()][neuerPruef.getY()] == PacmanTileType.WALL) flag[3] = true;
						for(int i = 0; i < flag.length; i++) {
							if(flag[i]) besetzteSeiten++;
						}
						last = pruef;
					}
					sackgassen.add(sackgasse);
				}
			}
		}
		return sackgassen;
	}
	
	
	static HashMap sortHashmap(HashMap hmap) {
		// von https://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/
		LinkedList list = new LinkedList(hmap.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o, Object v) {
				return ((Comparable) ((Map.Entry) (v)).getValue())
		                  .compareTo(((Map.Entry) (o)).getValue());
			}
		});
		
		HashMap result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
