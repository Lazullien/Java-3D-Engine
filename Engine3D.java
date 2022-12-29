package graphic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Game.InGamePanel;
import calculate.Vector3D;

public class Engine3D {
	
	public InGamePanel gp;
	
	Polygon po;
	
	Vector3D vCam,
	         vLookDir,
	         ls;
	
	//center is 0,0,0
	public int distance,
	           FOV,
	     	   w,
	    	   h;
	
	protected float n = 0.1f,
		            f = 1000.0f,
		            thetax = 0,
		            thetay = 0,
		            thetaz = 0, //for rotation somehow x is z, y is y, z is x
	                fYaw;
	
	ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	
	//size is s, s is basically like a radius, half size
	
	public Engine3D() {
	   FOV = 60;
	   vCam = new Vector3D(0,1,0);
	   ls = new Vector3D(0,1,0);
	   fYaw = 0;
	   w = 600;
	   h = 600;
	}
	
	public Engine3D(InGamePanel gp) {
	   this.gp = gp;
	   w = gp.getWidth();
	   h = gp.getHeight();
	   FOV = 60;
	   vCam = new Vector3D(0,0,0);
	   ls = new Vector3D(0,0,1);
	}
	
	public void loadMeshes(Graphics2D g2) {
		thetax++;
		thetay++;
		thetaz++;
		
	   Matrix4x4 proj = makeProj(FOV, w/h, n, f),
	             rx = rotateX(thetax),
	             rz = rotateZ(thetaz),
	             matTrans = makeTranslation(0,0,5),
	             matWorld = makeMatrixId();
	   
	   matWorld = multiplyMats(rz, rx);
	   matWorld = multiplyMats(matWorld, matTrans);
	   
	   Vector3D vUp = new Vector3D(0,1,0);
	   Vector3D vTarg = new Vector3D(0,0,1);
	   Matrix4x4 matCamRot = rotateY(fYaw);
	   vLookDir = multiplyVecMat(vTarg, matCamRot);
	   vTarg = vecAdd(vCam, vLookDir);
	   Matrix4x4 matCam = pointAt(vCam, vTarg, vUp);
	   
	   Matrix4x4 matView = matQuickInverse(matCam);
		   
	   for(int i = 0; i < meshes.size(); i++) {
		for(int t = 0; t < meshes.get(i).tris.length; t++) {
		
	     Triangle triProjected = new Triangle()
	            , triTransformed = new Triangle()
	            , triViewed = new Triangle();
	     
	     triTransformed.points[0] = multiplyVecMat(meshes.get(i).tris[t].points[0], matWorld);
	     triTransformed.points[1] = multiplyVecMat(meshes.get(i).tris[t].points[1], matWorld);
	     triTransformed.points[2] = multiplyVecMat(meshes.get(i).tris[t].points[2], matWorld);
	     
	     Vector3D normal, l1, l2;
	     
	     l1 = vecSub(triTransformed.points[1], triTransformed.points[0]);
	     l2 = vecSub(triTransformed.points[2], triTransformed.points[0]);
	              
	     normal = vecCrossPro(l1, l2);
	     normal = vecNormalize(normal);
	     
	     Vector3D camRay = vecSub(triTransformed.points[0], vCam);
	     
	     if(vecDotPro(normal, camRay) > 0.0f) {
	    	
	    	getShading(triTransformed, normal);
	    	triViewed.points[0] = multiplyVecMat(triTransformed.points[0], matView);
	    	triViewed.points[1] = multiplyVecMat(triTransformed.points[1], matView);
	    	triViewed.points[2] = multiplyVecMat(triTransformed.points[2], matView);
	    	
	    	triProjected.points[0] = multiplyVecMat(triViewed.points[0], proj);
	    	triProjected.points[1] = multiplyVecMat(triViewed.points[1], proj);
	    	triProjected.points[2] = multiplyVecMat(triViewed.points[2], proj);
	    	
	    	triProjected.points[0].x *= -1;
	    	triProjected.points[0].y *= -1;
	    	triProjected.points[1].x *= -1;
	    	triProjected.points[1].y *= -1;
	    	triProjected.points[2].x *= -1;
	    	triProjected.points[2].y *= -1;
	    	
	    	Vector3D offset = new Vector3D(1,1,0);
	    	triProjected.points[0] = vecAdd(triProjected.points[0], offset);
	    	triProjected.points[1] = vecAdd(triProjected.points[1], offset);
	    	triProjected.points[2] = vecAdd(triProjected.points[2], offset);
	    	
	    	triProjected.points[0].x *= 0.5f * w;
	    	triProjected.points[0].y *= 0.5f * h;
	    	triProjected.points[1].x *= 0.5f * w;
	    	triProjected.points[1].y *= 0.5f * h;
	    	triProjected.points[2].x *= 0.5f * w;
	    	triProjected.points[2].y *= 0.5f * h;
	    	
	    	triProjected.col = triTransformed.col;
	    	fillTri(triProjected.col, triProjected, g2);
	     }
		}
	   }
	}
	
	//Removes the hypotenuse of the org triangle
	public void drawTriRi(Triangle org, Triangle proj, Graphics2D g2) {
	   g2.setColor(Color.red);
	   
	   float difx1 = Math.abs(org.points[0].x - org.points[1].x),
			 dify1 = Math.abs(org.points[0].y - org.points[1].y),
			 difz1 = Math.abs(org.points[0].z - org.points[1].z),

			 difx2 = Math.abs(org.points[0].x - org.points[2].x),
			 dify2 = Math.abs(org.points[0].y - org.points[2].y),
			 difz2 = Math.abs(org.points[0].z - org.points[2].z),
					 
			 difx3 = Math.abs(org.points[1].x - org.points[2].x),
		     dify3 = Math.abs(org.points[1].y - org.points[2].y),
		     difz3 = Math.abs(org.points[1].z - org.points[2].z);
	   
	   float l = (float) Math.sqrt(difx1*difx1 + dify1*dify1 + difz1*difz1),
		     w = (float) Math.sqrt(difx2*difx2 + dify2*dify2 + difz2*difz2),
		     h = (float) Math.sqrt(difx3*difx3 + dify3*dify3 + difz3*difz3);
	   
	   if(l != Math.max(Math.max(w, h), l)) {
	   drawLine(proj.points[0], proj.points[1], g2); //1
	   }
	   if(h != Math.max(Math.max(w, h), l)) {
	   drawLine(proj.points[1], proj.points[2], g2); //3
	   }
	   if(w != Math.max(Math.max(w, h), l)) {
	   drawLine(proj.points[0], proj.points[2], g2); //2
	   }
	}
	
	public void drawTri(Triangle tri, Graphics2D g2) {
	   g2.setColor(Color.red);
	   drawLine(tri.points[0], tri.points[1], g2);
	   drawLine(tri.points[1], tri.points[2], g2);
	   drawLine(tri.points[0], tri.points[2], g2);
	}
	
	public void fillTri(BufferedImage image, Triangle tri, Graphics2D g2) {
	   
	}
	
	public void fillTri(Color color, Triangle tri, Graphics2D g2) {
	   po = new Polygon(new int[] {(int)tri.points[0].x, (int)tri.points[1].x, (int)tri.points[2].x},
                        new int[] {(int)tri.points[0].y, (int)tri.points[1].y, (int)tri.points[2].y},
		                3);
	   g2.setColor(color);
       g2.fillPolygon(po);		   
	}
	
	public void drawLine(Vector3D p1, Vector3D p2, Graphics2D g2) {
	   g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}
	
	/**linear interpolation of pixels**/
	public void interPx() {
	   
	}
	
	public void addCube(float x, float y, float z, float s, int id) {
	   Mesh cube = new Mesh();
	   cube.tris = new Triangle[] {
	    //front
		new Triangle(new Vector3D(x - s, y - s, z - s), new Vector3D(x - s, y - s, z + s), new Vector3D(x + s, y - s, z + s))
	   ,new Triangle(new Vector3D(x - s, y - s, z - s), new Vector3D(x + s, y - s, z + s), new Vector3D(x + s, y - s, z - s))
	    //right
	   ,new Triangle(new Vector3D(x + s, y - s, z - s), new Vector3D(x + s, y - s, z + s), new Vector3D(x + s, y + s, z + s))
	   ,new Triangle(new Vector3D(x + s, y - s, z - s), new Vector3D(x + s, y + s, z + s), new Vector3D(x + s, y + s, z - s))
	    //back
	   ,new Triangle(new Vector3D(x + s, y + s, z - s), new Vector3D(x + s, y + s, z + s), new Vector3D(x - s, y + s, z + s))
	   ,new Triangle(new Vector3D(x + s, y + s, z - s), new Vector3D(x - s, y + s, z + s), new Vector3D(x - s, y + s, z - s))
	    //left
	   ,new Triangle(new Vector3D(x - s, y + s, z - s), new Vector3D(x - s, y + s, z + s), new Vector3D(x - s, y - s, z + s))
	   ,new Triangle(new Vector3D(x - s, y + s, z - s), new Vector3D(x - s, y - s, z + s), new Vector3D(x - s, y - s, z - s))
	    //top
	   ,new Triangle(new Vector3D(x - s, y - s, z + s), new Vector3D(x - s, y + s, z + s), new Vector3D(x + s, y + s, z + s))
	   ,new Triangle(new Vector3D(x - s, y - s, z + s), new Vector3D(x + s, y + s, z + s), new Vector3D(x + s, y - s, z + s))
	    //bottom
	   ,new Triangle(new Vector3D(x - s, y + s, z - s), new Vector3D(x - s, y - s, z - s), new Vector3D(x + s, y - s, z - s))
	   ,new Triangle(new Vector3D(x - s, y + s, z - s), new Vector3D(x + s, y - s, z - s), new Vector3D(x + s, y + s, z - s))
	   };
	   if(id != 0) {
		  cube.rational = true;
		  cube.id = id;
	   }
	   else {
		  cube.rational = false;
	   }
	   cube.thrd = true;
	   meshes.add(cube);
	}
	
	void getShading(Triangle t, Vector3D normal) {
	   Vector3D ld = new Vector3D(ls.x, ls.y, ls.z);
	   ld = vecNormalize(ld);
       float dp = Math.max(0.1f, vecDotPro(ld, normal));
	   dp*=5;
	   switch((int)dp) {
	   case 0: t.col = new Color(255, 255, 255); break;
	   case 1: t.col = new Color(223, 223, 223); break;
	   case 2: t.col = new Color(198, 198, 198); break;
	   case 3: t.col = new Color(168, 168, 168); break;
	   case 4: t.col = new Color(138, 138, 138); break;
	   }
	}
	
	Matrix4x4 makeTranslation(float x, float y, float z) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m[0][0] = 1;
	   m.m[1][1] = 1;
	   m.m[2][2] = 1;
	   m.m[3][3] = 1;
	   m.m[0][3] = x;
	   m.m[1][3] = y;
	   m.m[2][3] = z;
	   return m;
	}
	
	Matrix4x4 rotateX(float theta) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m = new float[][] {
		     {1, 0, 0, 0},
		     {0, cos(theta), -sin(theta), 0},
		     {0, sin(theta), cos(theta), 0},
		     {0, 0, 0, 1}
	   };
	   return m;
	}
	
	Matrix4x4 rotateY(float theta) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m = new float[][] {
		     {cos(theta), 0, sin(theta), 0},
		     {0, 1, 0, 0},
		     {-sin(theta), 0, cos(theta), 0},
		     {0, 0, 0, 1}
	   };
	   return m;
	}
	
	Matrix4x4 rotateZ(float theta) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m = new float[][] {
		     {cos(theta), -sin(theta), 0 ,0},
		     {sin(theta), cos(theta), 0, 0},
		     {0, 0, 1, 0},
		     {0, 0, 0, 1}
	   };
	   return m;
	}
	
	Matrix4x4 makeMatrixId() {
	   Matrix4x4 m = new Matrix4x4();
	   m.m[0][0] = 1;
	   m.m[1][1] = 1;
	   m.m[2][2] = 1;
	   m.m[3][3] = 1;
	   return m;
	}
	
	Matrix4x4 makeProj(float fov, float aspectratio, float fnear, float ffar) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m = new float[][] {
	  	     {1 / (aspectratio * tan(fov/2)), 0, 0, 0},
		     {0, 1 / tan(fov/2), 0 , 0},
			 {0, 0, ffar / (ffar - fnear), -ffar*fnear / (ffar - fnear)},
			 {0, 0, 1, 0}
	   };  
	   return m;
	}
	
	Matrix4x4 multiplyMats(Matrix4x4 m1, Matrix4x4 m2) {
	   Matrix4x4 r = new Matrix4x4();
	   for(int d = 0; d < 4; d++) 
		   for(int c = 0; c < 4; c++) 
			   r.m[d][c] = m1.m[0][c]*m2.m[d][0] + m1.m[1][c]*m2.m[d][1] + m1.m[2][c]*m2.m[d][2] + m1.m[3][c]*m2.m[d][3];
	   return r;
	}
	
	Matrix4x4 matQuickInverse(Matrix4x4 mat) {
	   Matrix4x4 m = new Matrix4x4();
	   m.m[0][0] = mat.m[0][0];	m.m[0][1] = mat.m[1][0]; m.m[0][2] = mat.m[2][0]; m.m[0][3] = 0.0f;
	   m.m[1][0] = mat.m[0][1];	m.m[1][1] = mat.m[1][1]; m.m[1][2] = mat.m[2][1]; m.m[1][3] = 0.0f;
	   m.m[2][0] = mat.m[0][2];	m.m[2][1] = mat.m[1][2]; m.m[2][2] = mat.m[2][2]; m.m[2][3] = 0.0f;
	   m.m[3][0] = -(mat.m[3][0]*m.m[0][0] + mat.m[3][1]*m.m[1][0] + mat.m[3][2]*m.m[2][0]);
	   m.m[3][1] = -(mat.m[3][0]*m.m[0][1] + mat.m[3][1]*m.m[1][1] + mat.m[3][2]*m.m[2][1]);
	   m.m[3][2] = -(mat.m[3][0]*m.m[0][2] + mat.m[3][1]*m.m[1][2] + mat.m[3][2]*m.m[2][2]);
	   m.m[3][3] = 1.0f;
	   return m;
	}
	
	//perspective projection, i is in, o is out
	Vector3D multiplyVecMat(Vector3D i, Matrix4x4 m) {
	   Vector3D o = new Vector3D();
	   o.x = i.x * m.m[0][0] + i.y * m.m[0][1] + i.z * m.m[0][2] + m.m[0][3];
	   o.y = i.x * m.m[1][0] + i.y * m.m[1][1] + i.z * m.m[1][2] + m.m[1][3];
	   o.z = i.x * m.m[2][0] + i.y * m.m[2][1] + i.z * m.m[2][2] + m.m[2][3];
	   float w = i.x * m.m[3][0] + i.y * m.m[3][1] + i.z * m.m[3][2] + m.m[3][3];
	   
	   if(w != 0.0f) {
		  o = vecDiv(o, w);
	   }
	   return o;
	}
	
	Matrix4x4 pointAt(Vector3D pos, Vector3D targ, Vector3D up) {
	   Vector3D newForward = vecSub(targ, pos);
	   newForward = vecNormalize(newForward);
	   
	   Vector3D a = vecMul(newForward, vecDotPro(up, newForward));
	   Vector3D newUp = vecSub(up, a);
	   newUp = vecNormalize(newUp);
	   
	   Vector3D newRight = vecCrossPro(newUp, newForward);
	   
	   Matrix4x4 m = new Matrix4x4();
	   m.m[0][0] = newRight.x; m.m[1][0] = newRight.y; m.m[2][0] = newRight.z;
	   m.m[3][0] = 0.0f;
	   m.m[0][1] = newUp.x; m.m[1][1] = newUp.y; m.m[2][1] = newUp.z;
	   m.m[3][1] = 0.0f; 
	   m.m[0][2] = newForward.x; m.m[1][2] = newForward.y; m.m[2][2] = newForward.z;
	   m.m[3][2] = 0.0f;
	   m.m[0][3] = pos.x; m.m[1][3] = pos.y; m.m[2][3] = pos.z;
	   m.m[3][3] = 1.0f;
	   return m;
	}
	
	Vector3D vecAdd(Vector3D v1, Vector3D v2) {
	   return new Vector3D(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
	}
	
	Vector3D vecSub(Vector3D v1, Vector3D v2) {
	   return new Vector3D(v1.x-v2.x, v1.y-v2.y, v1.z-v2.z);
    }
	
	Vector3D vecMul(Vector3D v1, float k) {
	   return new Vector3D(v1.x*k, v1.y*k, v1.z*k);
    }
	
	Vector3D vecDiv(Vector3D v1, float k) {
	   return new Vector3D(v1.x/k, v1.y/k, v1.z/k);
    }
	
	float vecDotPro(Vector3D v1, Vector3D v2) {
	   return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}
	
	float vecLeng(Vector3D v) {
	   return (float)Math.sqrt(vecDotPro(v,v));
	}
	
	Vector3D vecNormalize(Vector3D v) {
	   float l = vecLeng(v);
	   return new Vector3D(v.x/l, v.y/l, v.z/l);
	}
	
	Vector3D vecCrossPro(Vector3D v1, Vector3D v2) {
	   Vector3D v = new Vector3D();
	   v.x = v1.y*v2.z - v1.z*v2.y;
	   v.y = v1.z*v2.x - v1.x*v2.z;
	   v.z = v1.x*v2.y - v1.y*v2.x;
	   return v;
	}
	
 class Matrix4x4{
	protected float m[][];
	
	public Matrix4x4() {
	   m = new float[4][4];
	}
 }
	
 public class Mesh{
    Triangle[] tris;
    boolean rational;
    int id;
    boolean thrd;
 }
 
   public float sin(float theta) {
	  return (float) Math.sin(Math.toRadians(theta)); 
   }
   public float cos(float theta) {
	  return (float) Math.cos(Math.toRadians(theta)); 
   }
   public float tan(float theta) {
	  return (float) Math.tan(Math.toRadians(theta));  
   }
}
