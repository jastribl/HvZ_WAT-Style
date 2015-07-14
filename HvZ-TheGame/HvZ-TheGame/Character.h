#pragma once
#include "BaseClass.h"
class World;
#include <queue>

class Character :public BaseClass {

private:
	std::queue<Location> destinationList;

	void move(const float x, const float y, const float z);
	bool hitDetect(const BaseClass* test);
	virtual void updateSprite();

public:
	Character(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point);
	virtual ~Character();

	void setDestination(sf::Vector3i& location);
	virtual bool fly();
	virtual void applyMove();
	virtual void stop();
	virtual void draw(sf::RenderWindow& window);

	std::vector<Location> getNeighbors(Location me);
	std::vector<Location> pathTo(Location start, Location end);
};