#pragma once

#include "Level.h"
#include <vector>

class World {

private:
	std::vector<Level> world;

public:
	World();
	~World();
	void addLevel(const Level& level);
	void removeLevel(int i);
	void draw(sf::RenderWindow& window);
	int size();
};