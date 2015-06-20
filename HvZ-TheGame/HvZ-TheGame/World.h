#pragma once
class BaseClass;
#include "Constants.h"
#include <string>

class World {

private:
	std::string name;
	std::multimap<sf::Vector3i, BaseClass*, ByLocation> world;
	std::vector<BaseClass*> deletedItems;

public:;
	   std::vector<BaseClass*> itemsToMove;

	   World(std::string name);
	   ~World();

	   bool itemsExistAtGridLocation(const sf::Vector3i& point) const;
	   std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> getItemsAtGridLocation(const sf::Vector3i& point);
	   void add(BaseClass* object);
	   void removeItemFromWorld(const sf::Vector3i& grid, const sf::Vector3f& point);
	   void deleteItemFromWorld(const sf::Vector3i& grid, const sf::Vector3f& point);
	   void clearDeletedItems();
	   void updateAndDraw(sf::RenderWindow& window);
};