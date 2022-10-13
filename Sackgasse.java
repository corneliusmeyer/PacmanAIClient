package de.fh.stud.pacmanFinal;

import java.util.LinkedList;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.enums.PacmanTileType;

public class Sackgasse {
	private LinkedList<Sackgassenpfad> gasse;
	
	Sackgasse() {
		gasse = new LinkedList<Sackgassenpfad>();
	}
	
	public void wegHinzufuegen(Sackgassenpfad pfad) {
		gasse.add(0, pfad);
		aktualisiereTiefe();
	}
	
	public void aktualisiereTiefe() {
		for(int tiefe = 1; tiefe < gasse.size(); tiefe++) {
			gasse.get(tiefe-1).tiefe = tiefe;
		}
	}
	
	public int getNumberOfDots() {
		int dots = 0;
		for(Sackgassenpfad sp : gasse) {
			if(sp.art == PacmanTileType.DOT) dots++;
		}
		return dots;
	}
	
	public boolean istPunktInSackgasse(Vector2 point) {
		for(Sackgassenpfad p : gasse) {
			if(p.point.equals(point)) return true;
		}
		return false;
	}
	
	public Sackgassenpfad getEingang() {
		return gasse.getFirst();
	}
	
	public int getTiefe() {
		return gasse.size();
	}
	
	public Vector2 getPunktVorEingang() {
		Sackgassenpfad eingang = getEingang();
		Vector2 result = Helper.getPunktNebenPosition(eingang.point, (eingang.richtung+2) % 4);
		return result;
	}
	
	public int getNumberOfDotsBisTiefe(int tiefe) {
		int count = 0;
		for(int i = 0; i < tiefe; i++) {
			if(gasse.get(i).art == PacmanTileType.DOT) count++;
		}
		return count;
	}
	
	public boolean istGeisterSackgasse(PacmanTileType[][] feld) {
		Sackgassenpfad letzter = gasse.getLast();
		Vector2 pos = Helper.getPunktNebenPosition(letzter.point, letzter.richtung);
		return Helper.istGhostAufPosition(feld, pos);
	}
	
	
	public LinkedList<Sackgassenpfad> getNodes() {
		return gasse;
	}
}
