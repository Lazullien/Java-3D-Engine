Based on Javidx9's tutorial on making a 3D engine.
Technically able to draw any 3D mesh given the triangles, but only capable of rendering by color without specific texture layering.

Run and render this in a gameloop of some kind. addCube() creates a preset cube for testing. Use drawTriRi() for a "debug" view, parameter "org" should be triViewed or triTransformed. drawTri() draws all triangles. Change vecDotPro(normal, camRay) > 0.0f to true to disable remove culling.
