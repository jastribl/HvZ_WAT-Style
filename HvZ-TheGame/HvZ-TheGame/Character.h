#pragma once

#include "Point.h"

class Character {

private:
	sf::Sprite sprite;
	int characterLevel;
	Point gridLocation;
	Point gridDestination;

public:
	Character(Point grid, Point point, int level, const sf::Texture& texture);
	~Character();
	Point getGridLocation() const;
	void setGridLocation(Point point);
	Point getGridDestination() const;
	void setGridDestination(Point point);

	void move(int x, int y);
	void stop();

	void draw(sf::RenderWindow& window);
};