#pragma once
class Item;
class TextureManager;

class Inventory {

private:
	std::vector<Item*> inventory;
	int activeIndex;

public:
	Inventory(TextureManager& textureManager);
	~Inventory();

	void click(int x, int y);
	void release(int x, int y);
	void drawToWindow(sf::RenderWindow& window);
};