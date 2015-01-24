#ifndef BULLET_H
#define BULLET_H
#include "Constants.h"
class Bullet : public sf::Sprite
{
private:
	sf::Vector2f velocity;
	bool done=false;
	int originx;
	int originy;
public:
	Bullet(const sf::Vector2f velo, const sf::Texture& tex, const sf::Vector2f pos)
	{
		velocity = velo;
		setTexture(tex);
		setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
		originx = tex.getSize().x / 2;
		originy = tex.getSize().y / 2;
		setPosition(pos);
	}
	void setVelocity(const sf::Vector2f& velo)
	{
		velocity = velo;
	}
	const sf::Vector2f getVelocity()
	{
		return velocity;
	}
	void fly()
	{
		move(velocity);
		int x = getPosition().x;
		int y = getPosition().y;
		if (x + originx < 0 || x - originx>WINDOW_X || y + originx < 0 || y - originx>WINDOW_Y)
			done = true;
	}
	void stop()
	{
		velocity = sf::Vector2f(0.0f, 0.0f);
	}
	void setDone(bool d)
	{
		done = d;
	}
	bool getDone()
	{
		return done;
	}
};
#endif