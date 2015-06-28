#include "stdafx.h"
#include "Inventory.h"
#include "Item.h"
#include "TextureManager.h"

Inventory::Inventory(TextureManager& textureManager) {
	activeIndex = -1;
	for (int i = 0; i < BOX_NUM; ++i) {
		Item* item = new Item(textureManager.getTextureFor(INVENTORY_ITEM, i));
		item->sprite.setPosition(BOX_ORIGIN_X + (i % BOX_PERROW) * BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y * (i / BOX_PERROW) + BOX_BORDER);
		inventory.push_back(item);
	}
}

Inventory::~Inventory() {}

void Inventory::click(int x, int y) {
	for (int z = 0; z < BOX_NUM; z++)
		std::cout << z << " " << inventory[z]->sprite.getGlobalBounds().width << " " << inventory[z]->sprite.getGlobalBounds().height << std::endl;
	for (int z = 0; z < BOX_NUM; z++) {
		if (inventory[z]->sprite.getGlobalBounds().contains(x, y)) {
			std::cout << "clicked:" << z << std::endl;
			inventory[z]->sprite.setPosition(x, y);
			inventory[z]->setActive();
			activeIndex = z;
			break;
		}
	}
}

void Inventory::release(int x, int y) {
	if (activeIndex != -1) {
		inventory[activeIndex]->release();
		bool found = false;
		for (int z = 0; z < BOX_NUM; z++) {
			if (z != activeIndex&&inventory[z]->sprite.getGlobalBounds().contains(x, y)) {
				std::cout << "released:" << z << std::endl;
				inventory[activeIndex]->sprite.setPosition(inventory[z]->sprite.getPosition());
				inventory[z]->sprite.setPosition(BOX_ORIGIN_X + (activeIndex%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(activeIndex / BOX_PERROW) + BOX_BORDER);
				Item* temp = inventory[activeIndex];
				inventory[activeIndex] = inventory[z];
				inventory[z] = temp;
				temp = NULL;
				delete temp;
				found = true;
				break;
			}
		}
		if (!found) {
			inventory[activeIndex]->sprite.setPosition(BOX_ORIGIN_X + (activeIndex%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(activeIndex / BOX_PERROW) + BOX_BORDER);
		}
		activeIndex = -1;
	}
}

void Inventory::drawToWindow(sf::RenderWindow& window) {
	std::vector<sf::Vertex> vertices;
	//Inventory border
	vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X - BOX_LGBORDER, BOX_ORIGIN_Y - BOX_LGBORDER), sf::Color(255, 216, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X - BOX_LGBORDER, BOX_ORIGIN_Y + BOX_SIZE_WIDTH + BOX_LGBORDER), sf::Color(255, 216, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + BOX_SIZE_LENGTH + BOX_LGBORDER, BOX_ORIGIN_Y + BOX_SIZE_WIDTH + BOX_LGBORDER), sf::Color(255, 216, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + BOX_SIZE_LENGTH + BOX_LGBORDER, BOX_ORIGIN_Y - BOX_LGBORDER), sf::Color(255, 216, 0)));
	//Inventory blocks
	for (int x = 0; x < BOX_NUM; x++) {
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW)), sf::Color(255, 216, 0)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_WIDTH), sf::Color(255, 216, 0)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_LENGTH, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_WIDTH), sf::Color(255, 216, 0)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_LENGTH, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW)), sf::Color(255, 216, 0)));

		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_BORDER), sf::Color(64, 64, 64)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_WIDTH - BOX_BORDER), sf::Color(50, 50, 50)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_LENGTH - BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_WIDTH - BOX_BORDER), sf::Color(50, 50, 50)));
		vertices.push_back(sf::Vertex(sf::Vector2f(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_LENGTH - BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_BORDER), sf::Color(64, 64, 64)));
	}
	window.draw(&vertices[0], vertices.size(), sf::Quads);
	for (int x = 0; x < 28; x++) {
		if (x != activeIndex)
			inventory[x]->draw(window);
	}
	if (activeIndex != -1) {
		sf::Vector2i temp = sf::Mouse::getPosition();
		inventory[activeIndex]->sprite.setPosition(temp.x, temp.y);
		inventory[activeIndex]->draw(window);
	}
}