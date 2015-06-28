#pragma once
#include "BaseClass.h"
class World;

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