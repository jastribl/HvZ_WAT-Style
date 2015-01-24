#ifndef OBSTACLE_H
#define OBSTACLE_H
class Obstacle : public sf::Sprite
{
public:
	Obstacle(const sf::Texture& tex, const sf::Vector2f pos, bool centre)
	{
		setTexture(tex);
		setPosition(pos);
		if (centre)
			setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
	}
	Obstacle(const sf::Texture& tex, float x, float y, bool centre)
	{
		setTexture(tex);
		if (centre)
			setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
		setPosition(sf::Vector2f(x, y));
	}
};
#endif