#ifndef OBSTACLE_H
#define OBSTACLE_H
class Obstacle : public sf::Sprite
{
public:
	Obstacle(const sf::Texture& tex, const sf::Vector2f pos)
	{
		setTexture(tex);
		setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
		setPosition(pos);
	}
	Obstacle(const sf::Texture& tex, float x,float y)
	{
		setTexture(tex);
		setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
		setPosition(sf::Vector2f(x,y));
	}
};
#endif