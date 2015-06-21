#pragma once
class World;
#include "Location.h"

class BaseClass {

protected:
	sf::Sprite sprite;
	World& world;
	Location location;
	sf::Vector3i gridTemp = sf::Vector3i();
	sf::Vector3f pointTemp = sf::Vector3f();

	virtual void move(const float x, const float y, const float z);

public:
	int itemGroup;

	BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup);
	~BaseClass();


	const sf::Vector3i& getGridLocation() const;
	const sf::Vector3f& getPointLocation() const;
	void setGridLocation(const sf::Vector3i& grid);
	void setPointLocaton(const sf::Vector3f& point);

	sf::FloatRect& hitBox();
	virtual void fly();
	void applyMove();
	virtual void draw(sf::RenderWindow& window);
};