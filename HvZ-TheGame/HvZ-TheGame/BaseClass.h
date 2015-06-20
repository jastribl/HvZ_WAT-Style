#pragma once
class World;

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;
	sf::Vector3i gridTemp = sf::Vector3i();
	sf::Vector3f pointTemp = sf::Vector3f();

	virtual void move(const float x, const float y, const float z);

public:
	int itemGroup;
	sf::Vector3i gridLoc;
	sf::Vector3f pointLoc;

	BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& gridLoc, const sf::Vector3f& pointLoc, int itemGroup);
	~BaseClass();

	sf::FloatRect& hitBox();
	virtual void fly();
	void applyMove();
	virtual void draw(sf::RenderWindow& window);
};