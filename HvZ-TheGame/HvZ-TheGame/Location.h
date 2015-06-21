#pragma once
class Location {

private:
	sf::Vector3i grid;
	sf::Vector3f point;

public:
	Location();
	Location(sf::Vector3i gridLoc, sf::Vector3f pointLoc);
	~Location();

	const sf::Vector3i& getGrid() const;
	const sf::Vector3f& getPoint() const;
	void setGrid(const sf::Vector3i& grid);
	void setPoint(const sf::Vector3f& point);
	void add(float x, float y, float z);
};