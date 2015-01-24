#ifndef CHARACTER_H
#define CHARACTER_H
#include "MovingObject.h"
class Character : public MovingObject
{
public:
	Character(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y,bool centre) :MovingObject(velo, tex, x, y,centre)
	{
	}
	Character(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos, bool centre) :MovingObject(velo, tex, pos,centre)
	{
	}
};
#endif