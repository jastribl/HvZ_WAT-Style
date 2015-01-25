#ifndef OBSTACLE_H
#define OBSTACLE_H
#include "Object.h"
class Obstacle : public Object
{
public:
	Obstacle(int id, const sf::Texture& tex, const sf::Vector2f& pos, bool centre) :Object(id,tex, pos, centre)
	{
	}
	Obstacle(int id, const sf::Texture& tex, float x, float y, bool centre) :Object(id,tex, x, y, centre)
	{
	}
};
#endif