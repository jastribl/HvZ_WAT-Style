#ifndef CHARACTER_H
#define CHARACTER_H
class Character : public sf::Sprite
{
private:
	sf::Vector2f velocity;
public:
	Bullet(const sf::Vector2f& velo, const sf::Texture& tex, const sf::Vector2f& pos)
	{
		velocity = velo;
		setTexture(tex);
		setOrigin(tex.getSize().x / 2, tex.getSize().y / 2);
		setPosition(pos);
	} // private default constructor
	void setVelocity(const sf::Vector2f& velo)
	{
		velocity = velo;
	}
	const sf::Vector2f& getVelocity()
	{
		return velocity;
	}
	void fly()
	{
		move(velocity);
	}
};
#endif