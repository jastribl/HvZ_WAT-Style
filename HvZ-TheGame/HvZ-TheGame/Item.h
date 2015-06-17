#pragma once

class Item {

private:
	bool active;

public:
	sf::Sprite sprite;

	Item(const sf::Texture& texture);
	~Item();

	void setActive();
	void release();
	bool isActive();
	void draw(sf::RenderWindow& window);
};

