package de.fh.stud.pacmanFinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.fh.kiServer.util.Util;
import de.fh.kiServer.util.Vector2;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.GhostType;
import de.fh.pacman.enums.PacmanTileType;

public class Bewertungen {
	final static double BEWERTUNG_FELD_DOT = 0.5;
	final static double BEWERTUNG_FELD_EMPTY = 0.1;
	final static double BEWERTUNG_FELD_GHOST = -0.0;
	final static double BEWERTUNG_FELD_POWERPILL = 0.85;
	final static double BEWERTUNG_FELD_GHOST_AND_POWERPILL = -0.0;
	final static double BEWERTUNG_FELD_GHOST_AND_DOT = -0.0;
	final static double BEWERTUNG_FELD_WALL = -9999.9;
	final static double BEWERTUNG_LEERE_SACKGASSE = -0.9;
	final static double BEWERTUNG_GHOST_RESPAWN = -0.6;
	final static double BEWERTUNG_GHOST_PASSIVE_DIST_2 = -0.4;
	final static double BEWERTUNG_GHOST_PASSIVE_DIST_1 = -1.0;
	final static double BEWERTUNG_GHOST_HUNTER_DIST_3 = -0.4;
	final static double BEWERTUNG_GHOST_HUNTER_DIST_2 = -0.88;
	final static double BEWERTUNG_GHOST_HUNTER_DIST_1 = -1.0;
	final static double BEWERTUNG_GHOST_RANDOM_DIST_2 = -0.5;
	final static double BEWERTUNG_GHOST_RANDOM_DIST_1 = -0.8; 
	final static double BEWERTUNG_GHOST_EAGER_DIST_2 = -0.7;
	final static double BEWERTUNG_GHOST_EAGER_DIST_1 = -1.0;
	final static double BEWERTUNG_GHOST_IGNORE_DOT = -0.5;
	
	final static double BEWERTUNG_PACMAN_NEARANCE = 0.65;
	final static double BEWERTUNG_DOT_NEARANCE = 0.35;
	
	public static boolean ignoreGhost = false;
	
	static public double[][] bewerteFeldTileType(PacmanTileType[][] feld, int isPillMode) {
		double[][] relevanz = new double[feld.length][feld[0].length];
		for(int x = 0; x < feld.length; x++) {
			for(int y = 0; y < feld[0].length; y++) {
				switch(feld[x][y]) {
					case DOT: {
						relevanz[x][y] = BEWERTUNG_FELD_DOT;
						break;
					}
					case WALL: {
						relevanz[x][y] = BEWERTUNG_FELD_WALL;
						break;
					}
					case EMPTY: {
						relevanz[x][y] = BEWERTUNG_FELD_EMPTY;
						break;
					}
					case GHOST: {
						relevanz[x][y] = BEWERTUNG_FELD_GHOST * isPillMode;
						break;
					}
					case POWERPILL: {
						relevanz[x][y] = BEWERTUNG_FELD_POWERPILL;
						break;
					}
					case GHOST_AND_POWERPILL: {
						relevanz[x][y] = BEWERTUNG_FELD_GHOST_AND_POWERPILL * isPillMode;
						break;
					}
					case GHOST_AND_DOT: {
						relevanz[x][y] = BEWERTUNG_FELD_GHOST_AND_DOT * isPillMode;
						break;
					}
				}
			}
		}
		return relevanz;
	}
	
	
	
	static public double[][] bewerteGeisterDistanz(PacmanPercept percept, PacmanTileType[][] feld) {
		double[][] relevanz = new double[feld.length][feld[0].length];
		percept.getGhostInfos().forEach(g -> {
			int pill = g.getPillTimer() > 2 ? -1 : 1;
			Vector2 pos = g.getPos();
			if(g.isDead()) {
				pos = g.getSpawn();
			}
			for(int x = 0; x < feld.length; x++) {
				for(int y = 0; y < feld[0].length; y++) {
					int dist = Util.manhattan(x, y, pos.getX(), pos.getY());
					if(g.getType().equals(GhostType.GHOST_PASSIVE.toString())) {
						if(dist <= 2) {
							if(dist == 1) 
								relevanz[x][y] += BEWERTUNG_GHOST_PASSIVE_DIST_1 * pill;
							else relevanz[x][y] += BEWERTUNG_GHOST_PASSIVE_DIST_2 * pill;
						}
					}
					else if(g.getType().equals(GhostType.GHOST_HUNTER.toString())) {
						if(dist <= 3) {
							if(dist <= 2) {
								if(dist == 1) {
									relevanz[x][y] += BEWERTUNG_GHOST_HUNTER_DIST_1 * pill;
								}
								else relevanz[x][y] += BEWERTUNG_GHOST_HUNTER_DIST_2 * pill;
							} else relevanz[x][y] += BEWERTUNG_GHOST_HUNTER_DIST_3 * pill;
						}
					}
					else if(g.getType().equals(GhostType.GHOST_RANDOM.toString())) {
						if(dist <= 2) {
							if(dist == 1) 
								relevanz[x][y] += BEWERTUNG_GHOST_RANDOM_DIST_1 * pill;
							else relevanz[x][y] += BEWERTUNG_GHOST_RANDOM_DIST_2 * pill;
						}
					}
					else if(g.getType().equals(GhostType.GHOST_EAGER.toString())) {
						if(dist <= 2) {
							if(dist == 1) {
								relevanz[x][y] += BEWERTUNG_GHOST_EAGER_DIST_1 * pill;
							}
							else relevanz[x][y] += BEWERTUNG_GHOST_EAGER_DIST_2 * pill;
						}
					}                                         
				}
			}
		});
		return relevanz;
	}
	
	
	
	static public double[][] bewerteDotDistanz(ArrayList<Vector2> dots, PacmanTileType[][] feld) {
		double[][] relevanz = new double[feld.length][feld[0].length];
		HashMap<Vector2, Integer> dotFeldBewertung = new HashMap<Vector2, Integer>();
		for(int x = 0; x < feld.length; x++) {
			for(int y = 0; y < feld[0].length; y++) {
				if(feld[x][y] == PacmanTileType.WALL) continue;
				int value = 0;
				for(Vector2 b: dots) {
					value += Util.manhattan(x, y, b.x, b.y);
				}
				dotFeldBewertung.put(new Vector2(x, y), value);
			}
		}
		dotFeldBewertung = Helper.sortHashmap(dotFeldBewertung);
		int size = dotFeldBewertung.size();
		Set set = dotFeldBewertung.entrySet();
	    Iterator iterator = set.iterator();
	    int index = 0;
	    while(iterator.hasNext()) {
	    	index++;
	    	Map.Entry me = (Map.Entry)iterator.next();
	    	Vector2 mapvec = (Vector2) me.getKey();
	    	double wert =  ((double)index/(double)size) * BEWERTUNG_DOT_NEARANCE;
	    	relevanz[mapvec.x][mapvec.y] = wert;
	    }
		return relevanz;
	}
	
	
	
	static public double[][] bewerteDistanzDotZuPacman(ArrayList<Vector2> dots, Vector2 myPos, PacmanTileType[][] feld) {
		double[][] relevanz = new double[feld.length][feld[0].length];
		HashMap<Vector2, Integer> dotFeldBewertung = new HashMap<Vector2, Integer>();
		for(Vector2 b: dots) {
			dotFeldBewertung.put(b, Util.manhattan(myPos.x, myPos.y, b.x, b.y));
		}		
		dotFeldBewertung = Helper.sortHashmap(dotFeldBewertung);
		int size = dotFeldBewertung.size();
		Set set = dotFeldBewertung.entrySet();
	    Iterator iterator = set.iterator();
	    int index = 0;
	    while(iterator.hasNext()) {
	    	index++;
	    	Map.Entry me = (Map.Entry)iterator.next();
	    	Vector2 mapvec = (Vector2) me.getKey();
	    	double wert =  ((double)index/(double)size) * BEWERTUNG_PACMAN_NEARANCE;
	    	relevanz[mapvec.x][mapvec.y] += wert;
	    }
		return relevanz;
	}
	
	public static double[][] bewerteSackgassen(PacmanTileType[][] feld, PacmanPercept percept, int dotcount) {
		ignoreGhost = false;
		double[][] relevanz = new double[feld.length][feld[0].length];
		Vector2 myPos = percept.getPosition();
		ArrayList<Sackgasse> sackgassen = Helper.berechneSackgassen(feld);
		outer : for(Sackgasse sg : sackgassen) {
			boolean istGeisterSackgasse = sg.istGeisterSackgasse(feld);
			LinkedList<Sackgassenpfad> nodes = sg.getNodes();
			for(Sackgassenpfad node : nodes) {
				if(Helper.istGhostAufPosition(node.art)) {
					for(Sackgassenpfad bewerte : nodes) {
						Vector2 v = bewerte.point;
						relevanz[v.x][v.y] = -99.0;
					}
					continue outer;
				}
			}
			if(sg.getNumberOfDots() == 0) {
				for(Sackgassenpfad node : nodes) {
					Vector2 v = node.point;
					relevanz[v.x][v.y] = Bewertungen.BEWERTUNG_LEERE_SACKGASSE;
				}
				continue;
			} else if(sg.getNumberOfDots() == dotcount && !istGeisterSackgasse) {
				for(Sackgassenpfad node : nodes) {
					Vector2 v = node.point;
					relevanz[v.x][v.y] = 1.0;
				}
				continue;
			} else {
				Vector2 eingang = sg.getPunktVorEingang();
				boolean istPacmanInSackgasse = sg.istPunktInSackgasse(myPos);
				if(istGeisterSackgasse && istPacmanInSackgasse) {
					ignoreGhost = true;
					relevanz[eingang.x][eingang.y] = 99999.9;
					return relevanz;
				}
				int erlaubteTiefe = 0;
				if(!istGeisterSackgasse) {
					erlaubteTiefe = (Helper.getDistanzZuHunter(percept, eingang)-1)/2;
				}
				else {
					for(Sackgassenpfad node : nodes) {
						Vector2 v = node.point;
						relevanz[v.x][v.y] = -99.0;
					}
					if(istPacmanInSackgasse) {
						ignoreGhost = true;
						relevanz[eingang.x][eingang.y] = 99.9;
					}
					continue;
				}
				if(erlaubteTiefe > nodes.size()) erlaubteTiefe = nodes.size();
				if(sg.getNumberOfDotsBisTiefe(erlaubteTiefe) == 0) {
					for(Sackgassenpfad node : nodes) {
						Vector2 v = node.point;
						relevanz[v.x][v.y] = Bewertungen.BEWERTUNG_LEERE_SACKGASSE;
					}
					if(istPacmanInSackgasse) {
						ignoreGhost = true;
						relevanz[eingang.x][eingang.y] = 99.9;
					}
					continue;
				}
				if(istPacmanInSackgasse) {
					ignoreGhost = true;
					//System.out.println("Pacman ist in Sackgasse");
					for(Sackgassenpfad node : nodes) {
						if(node.point.equals(myPos)) {
							if(node.tiefe < erlaubteTiefe) {
								if(sg.getNumberOfDotsBisTiefe(erlaubteTiefe) > 0) {
									Vector2 v = nodes.get(erlaubteTiefe-1).point;
									relevanz[v.x][v.y] = 999.9;
								}
								else {
									relevanz[eingang.x][eingang.y] = 999.9;
								}
							}
							else {
								relevanz[eingang.x][eingang.y] = 999.9;
							}
							break;
						}
					}
					return relevanz;
				}
				else {
					boolean flip = false;
					for(Sackgasse check : sackgassen) {
						if(check.istPunktInSackgasse(myPos)) flip = true;
					}
					if(flip == false) {
						Vector2 v = nodes.get(erlaubteTiefe-1).point;
						relevanz[v.x][v.y] = 999.9;
					}
				}
				
			}
		}
		return relevanz;
	}
}
