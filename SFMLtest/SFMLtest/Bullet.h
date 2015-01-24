#ifndef BULLET_H
#define BULLET_H
#include "MovingObject.h"
class Bullet : public MovingObject
{
public:
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y,float angle) :MovingObject(velo, tex, x, y)
	{
		setRotation(angle);
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos, float angle) :MovingObject(velo, tex,pos)
	{
		setRotation(angle);
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, float x, float y) :MovingObject(velo,tex, x, y)
	{
	}
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos) :MovingObject(velo, tex, pos)
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