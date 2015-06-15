#pragma once

class Item :public sf::Sprite {

private:
	bool initialized;
	bool active;
	sf::Texture texture;

public:
	Item();
	~Item();

	void initalize(std::string file);
	void setActive();
	void release();
	bool isActive();
	bool isInitialized();
};

