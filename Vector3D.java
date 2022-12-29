package calculate;

import entities.Entity;
import tiles.Tile;

public class Vector3D{
    public float x, y, z, w;   
	public Vector3D(float x, float y, float z) {
	   this.x = x;
	   this.y = y;
	   this.z = z;
	}
	public Vector3D() {}
	public Vector3D(Vector3D in){
	   this.x = in.x;
	   this.y = in.y;
	   this.z = in.z;
	}
}
