#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

private:
	sf::Vector3f destLoca = sf::Vector3f();

	virtual	void move(const float x, const float y, const float z);
	bool hitDetect(const BaseClass* test);
	void stop();

public:
	Character(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation);
	~Character();

	void setDestination(const sf::Vector3f& dest);
	virtual void fly();
	virtual void draw(sf::RenderWindow& window);
};