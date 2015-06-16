#pragma once
class World;

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;

public:
	int itemGroup;
	sf::Vector3i gridLocation;
	sf::Vector3f pointLocation;

	BaseClass(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3f pointLocation, int itemGroup);
	~BaseClass();

	virtual void move(int x, int y, int z);
	virtual void hitDetect(BaseClass& test);
	virtual void draw(sf::RenderWindow& window);
};