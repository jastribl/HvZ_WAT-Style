#ifndef ALGORITHMS_H
#define ALGORITHMS_H
#include "Constants.h"

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
#endif