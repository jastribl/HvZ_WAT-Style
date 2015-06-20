#pragma once
class Location {

public:
	sf::Vector3i gridLoc;
	sf::Vector3f pointLoc;
	Location(sf::Vector3i gridLoc, sf::Vector3f pointLoc);
	~Location();
};