#pragma once
#include "BaseClass.h"
class World;

typedef std::multimap<sf::Vector3i, BaseClass*, ByLocation> WorldMap;
typedef std::pair <WorldMap::iterator, WorldMap::iterator> WorldMapRange;

class Bullet :public BaseClass {

private:
	int count = 0;
	sf::Vector2f velocity;

	void move(const float x, const float y, const float z);
	bool hitDetect(const BaseClass* test);

public:
	Bullet(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation, float speed, float angle);
	virtual ~Bullet();

	virtual void fly();
};