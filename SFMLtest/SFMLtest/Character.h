#ifndef CHARACTER_H
#define CHARACTER_H
#include "MovingObject.h"
class Character : public MovingObject
{
public:
	Character(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y) :MovingObject(velo, tex, x, y)
	{
	}
	Character(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos) :MovingObject(velo, tex, pos)
	{
	}
};
#endif