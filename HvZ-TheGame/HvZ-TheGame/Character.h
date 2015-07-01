#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

private:
	sf::Vector3i destination = sf::Vector3i();

	void move(const float x, const float y, const float z);
	bool hitDetect(const BaseClass* test);
	virtual void updateSprite();

public:
	Character(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point);
	virtual ~Character();

	void setDestination(const sf::Vector3f& dest);
	virtual bool fly();
	virtual void applyMove();
	virtual void draw(sf::RenderWindow& window);
};