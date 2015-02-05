#ifndef ALGORITHMS_H
#define ALGORITHMS_H
#include "Constants.h"
#include "Point.h"
float getDirectionAngle(float xfinal, float yfinal, float xstart, float ystart)
{
	return (float)(atan2(yfinal - ystart, xfinal - xstart) * 180.0 / M_PI);
}
float getDistance(float xdelta, float ydelta)
{
	return sqrtf((xdelta*xdelta +ydelta*ydelta));
}
float getDistance(float xfinal, float yfinal, float xstart, float ystart)
{
	return getDistance(xfinal-xstart,yfinal-ystart);
}
sf::Vector2f getDirectionVector(float xfinal, float yfinal, float xstart, float ystart, float scale = 1.0f)
{
	float xdir = xfinal - xstart;
	float ydir = yfinal - ystart;
	float len = getDistance(xdir, ydir);
	return sf::Vector2f(xdir*scale / len, ydir*scale / len);
}
Point iso2car(Point poi,int cut)
{
	if (cut==2)
		return Point{ round((poi.x - poi.y)*TILE_X + WINDOW_X_HALF),round( (poi.x + poi.y)*TILE_Y) };
	if (cut == 1)
		return Point{ (int)((poi.x - poi.y)*TILE_X + WINDOW_X_HALF), (int)((poi.x + poi.y)*TILE_Y) };
	return Point{ (poi.x - poi.y)*TILE_X + WINDOW_X_HALF, (poi.x + poi.y)*TILE_Y};
}
Point car2iso(Point poi, int cut)
{
	if (cut==2)
		return Point{round( ((poi.x - WINDOW_X_HALF - TILE_X) / TILE_X + (poi.y - TILE_Z) / TILE_Y) / 2.0),round( ((poi.y - TILE_Z) / TILE_Y - (poi.x - WINDOW_X_HALF - TILE_X) / TILE_X) / 2.0) };
	if (cut==1)
		return Point{ (int)(((poi.x - WINDOW_X_HALF - TILE_X) / TILE_X + (poi.y - TILE_Z) / TILE_Y) / 2.0), (int)(((poi.y - TILE_Z) / TILE_Y - (poi.x - WINDOW_X_HALF - TILE_X) / TILE_X) / 2.0) };
	return Point{ ((poi.x - WINDOW_X_HALF - TILE_X) / TILE_X + (poi.y - TILE_Z) / TILE_Y) / 2.0,((poi.y - TILE_Z) / TILE_Y - (poi.x - WINDOW_X_HALF - TILE_X) / TILE_X) / 2.0 };
}
#endif