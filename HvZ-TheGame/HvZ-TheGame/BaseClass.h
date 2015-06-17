#pragma once
class World;

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;

	virtual void hitDetect(BaseClass& test);

public:
	int itemGroup;
	sf::Vector3i gridLocation;
	sf::Vector3f pointLocation;

	BaseClass(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3f pointLocation, int itemGroup);
	~BaseClass();

	virtual void move(float x, float y, float z);
	virtual void draw(sf::RenderWindow& window);
};