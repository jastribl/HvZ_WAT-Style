#pragma once
class BaseClass;
#include <string>

typedef std::multimap<sf::Vector3i, BaseClass*, ByLocation> WorldMap;
typedef std::pair <WorldMap::iterator, WorldMap::iterator> WorldMapRange;

class World {

private:
	std::string name = "";
	WorldMap world;

public:;
	   std::vector<BaseClass*> itemsToMove;

	   World(std::string name);
	   ~World();

	   bool itemsExistAtGridLocation(const sf::Vector3i& grid) const;
	   WorldMapRange getItemsAtGridLocation(const sf::Vector3i& grid);
	   void add(BaseClass* object);
	   void removeItemFromWorld(const BaseClass* object);
	   void updateAndDraw(sf::RenderWindow& window);
};