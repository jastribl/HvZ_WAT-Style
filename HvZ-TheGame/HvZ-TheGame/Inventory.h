#pragma once
#include "Item.h"
class Inventory
{
private:
	std::vector<Item*> inventory;
	int activeIndex;
public:
	Inventory();
	~Inventory();
	void click(int x, int y);
	void release(int x,int y);
	void drawToWindow(sf::RenderWindow& window);
};

