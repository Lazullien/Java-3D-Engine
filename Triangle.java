package graphic;

import java.awt.Color;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

import calculate.Vector3D;

public class Triangle{
	Vector3D[] points;
	Color col;
	Triangle(Vector3D vec1, Vector3D vec2, Vector3D vec3) {
	   points = new Vector3D[] {vec1, vec2, vec3};
	}
	Triangle() {
	   points = new Vector3D[3];
	   points[0] = new Vector3D();
	   points[1] = new Vector3D();
	   points[2] = new Vector3D();
	}
}
