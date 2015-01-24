#ifndef BULLET_H
#define BULLET_H
#include "MovingObject.h"
class Bullet : public MovingObject
{
public:
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y, float angle, bool centre) :MovingObject(velo, tex, x, y,centre)
	{
		setRotation(angle);
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos, float angle, bool centre) :MovingObject(velo, tex, pos, centre)
	{
		setRotation(angle);
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y, bool centre) :MovingObject(velo, tex, x, y, centre)
	{
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos, bool centre) :MovingObject(velo, tex, pos, centre)
	{
	}
	void fly()
	{
		move(velocity);
		int x = (int)(getPosition().x);
		int y = (int)(getPosition().y);
		if (x + originx < 0 || x - originx>WINDOW_X || y + originx < 0 || y - originx>WINDOW_Y)
			done = true;
	}
};
#endif