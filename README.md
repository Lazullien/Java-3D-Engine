credits to javidx9 3d engine tutorial on youtube, couldn't have done it without him, probably no one will see this but
for those who do and wanna learn about 3d graphics go check him out, he does very clear explanations of these

How to use:
 You will need a custom gameloop panel, or if you wanna use mine which is a simple delta based loop
 is fine, you could also use a thread.sleep approach but that's not the main thing
 
 if you're using my test simply run it, not much need for explanation here
 
 if you're using your own panel, remember to instantiate Engine3D and call the addCube() method to add a cube
 
 to change the triangle into triangle debug mode, use drawTriRi() for version without the hypotenuse, the parameter org
 should be the triViewed, if it doesn't work try triTransformed, if nothing works abandon the method, drawTri() is the 
 version where all triangles are drawn, if you wanna remove culling change vecDotPro(normal, camRay)>0.0f to true
 
 also clean the unused imports they're just things i was too lazy to get rid of
 
 that's it
 
 
 also https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT?si=38f62c7ec03e4479 this song is fire
