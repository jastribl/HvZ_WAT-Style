#ifndef CHARACTER_H
#define CHARACTER_H
#include "MovingObject.h"
#include "Point.h"
class Character : public MovingObject
{
public:
	Character(int id, const sf::Vector2f& velo, const sf::Texture& tex, float x, float y, bool centre) :MovingObject(id,velo, tex, x, y, centre)
	{
	}
	Character(int id, const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos, bool centre) :MovingObject(id,velo, tex, pos, centre)
	{
	}

};
#endif