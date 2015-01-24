#ifndef MOVINGOBJECT_H
#define MOVINGOBJECT_H
#include "Object.h"
#include "Constants.h"
class MovingObject :public Object
{
protected:
	sf::Vector2f velocity;
public:
	MovingObject(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos) :Object(tex, pos, true)
	{
		velocity = velo;
	}
	MovingObject(const sf::Vector2f& velo, const sf::Texture& tex, float x,float y) :Object(tex, x,y, true)
	{
		velocity = velo;
	}
	void setVelocity(const sf::Vector2f& velo)
	{
		velocity = velo;
	}
	sf::Vector2f getVelocity()
	{
		return velocity;
	}
	void fly()
	{
		move(velocity);
	}
	void stop()
	{
		velocity = sf::Vector2f(0.0f, 0.0f);
	}
};
#endif