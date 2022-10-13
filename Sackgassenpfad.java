package de.fh.stud.pacmanFinal;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.enums.PacmanTileType;

public class Sackgassenpfad {
	int richtung;
	Vector2 point;
	PacmanTileType art;
	int tiefe;
	
	Sackgassenpfad(Vector2 point, int richtung, PacmanTileType art) {
		this.point = point;
		this.richtung = richtung;
		this.art = art;
	}
	
	public void setzeTiefe(int tiefe) {
		this.tiefe = tiefe;
	}

}
