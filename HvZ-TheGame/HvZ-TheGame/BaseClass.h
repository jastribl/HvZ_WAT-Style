#pragma once
class World;
#include "Location.h"

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;

	virtual void move(const float x, const float y, const float z);

public:
	const int itemType;
	Location loc;
	Location tempLoc;

	BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup);
	~BaseClass();

	sf::FloatRect& hitBox();
	virtual void fly();
	void applyMove();
	virtual void draw(sf::RenderWindow& window);
};