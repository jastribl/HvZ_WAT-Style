#pragma once
class World;
#include "Location.h"

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;
	virtual void updateSprite();

public:
	bool needsToBeDeleted = false;
	const int itemType;
	Location loc;
	Location tempLoc;

	BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup);
	virtual ~BaseClass();

	sf::FloatRect& screenHitBox();
	virtual void fly();
	virtual void applyMove();
	void stop();
	virtual void draw(sf::RenderWindow& window);
};