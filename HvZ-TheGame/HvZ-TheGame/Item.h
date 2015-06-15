#pragma once
class Item:public sf::Sprite
{
private:
	bool initialized;
	bool active;
	sf::Texture texture;
public:
	Item();
	
	void initalize(std::string file);
	~Item();
	void setActive();
	void release();
	bool isActive();
	bool isInitialized();
};

