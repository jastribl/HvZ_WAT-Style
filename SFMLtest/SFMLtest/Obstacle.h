#ifndef OBSTACLE_H
#define OBSTACLE_H
#include "Object.h"
class Obstacle : public Object
{
public:
	Obstacle(const sf::Texture& tex, const sf::Vector2f& pos, bool centre) :Object(tex,pos,centre)
	{
	}
	Obstacle(const sf::Texture& tex, float x, float y, bool centre) :Object(tex,x,y,centre)
	{
	}
};
#endif