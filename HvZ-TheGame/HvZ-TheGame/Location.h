#pragma once
class Location {

private:
	sf::Vector3i grid;
	sf::Vector3f point;

public:
	Location(sf::Vector3i gridLoc, sf::Vector3f pointLoc);
	~Location();

	const sf::Vector3i& getGridLocation() const;
	const sf::Vector3f& getPointLocation() const;
	void setGridLocation(const sf::Vector3i& grid);
	void setPointLocaton(const sf::Vector3f& point);
};