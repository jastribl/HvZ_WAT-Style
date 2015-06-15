#include "stdafx.h"
#include "Inventory.h"
#include "Item.h"
#include "Constants.h"
#include <iostream>

Inventory::Inventory() {
	activeIndex = -1;
	inventory = std::vector<Item*>(BOX_NUM);
	for (int x = 0; x < BOX_NUM; x++) {
		inventory[x] = new Item();
		inventory[x]->setPosition(BOX_ORIGIN_X + (x%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(x / BOX_PERROW) + BOX_BORDER);
	}
	for (int x = 0; x < 2; x++) {
		//inventory[x]->initalize("Resources/Images/nerf"+std::to_string(x%2)+".jpg");
		inventory[x]->initalize("Resources/Images/nerf" + std::to_string(x % 2) + ".jpg");
		inventory[x]->setScale(((float) (BOX_WIDTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().height), ((float) (BOX_LENGTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().width));
	}
	for (int x = 2; x < 8; x++) {
		//inventory[x]->initalize("Resources/Images/nerf"+std::to_string(x%2)+".jpg");
		inventory[x]->initalize("Resources/Images/special" + std::to_string((x - 2) % 6) + ".png");
		inventory[x]->setScale(((float) (BOX_WIDTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().height), ((float) (BOX_LENGTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().width));
	}
	for (int x = 8; x < inventory.size(); x++) {
		//inventory[x]->initalize("Resources/Images/nerf"+std::to_string(x%2)+".jpg");
		inventory[x]->initalize("Resources/Images/inventorybox.png");
		inventory[x]->setScale(((float) (BOX_WIDTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().height), ((float) (BOX_LENGTH - 2 * BOX_BORDER) / inventory[x]->getTextureRect().width));
	}
}

Inventory::~Inventory() {
	for (int x = 0; x < inventory.size(); x++)
		delete inventory[x];
}

void Inventory::click(int x, int y) {
	for (int z = 0; z < inventory.size(); z++)
		std::cout << z << " " << inventory[z]->getGlobalBounds().width << " " << inventory[z]->getGlobalBounds().height << std::endl;
	for (int z = 0; z < inventory.size(); z++) {
		if (inventory[z]->isInitialized() && inventory[z]->getGlobalBounds().contains(x, y)) {
			std::cout << "clicked:" << z << std::endl;
			inventory[z]->setPosition(x, y);
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
		for (int z = 0; z < inventory.size(); z++) {
			if (z != activeIndex&&inventory[z]->getGlobalBounds().contains(x, y)) {
				std::cout << "released:" << z << std::endl;
				inventory[activeIndex]->setPosition(inventory[z]->getPosition());
				inventory[z]->setPosition(BOX_ORIGIN_X + (activeIndex%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(activeIndex / BOX_PERROW) + BOX_BORDER);
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
			inventory[activeIndex]->setPosition(BOX_ORIGIN_X + (activeIndex%BOX_PERROW)*BOX_MOVEMENT_X + BOX_BORDER, BOX_ORIGIN_Y + BOX_MOVEMENT_Y*(activeIndex / BOX_PERROW) + BOX_BORDER);
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
	for (int x = 0; x < inventory.size(); x++) {
		if (x == activeIndex || !inventory[x]->isInitialized())
			continue;
		window.draw(*inventory[x]);
	}
	if (activeIndex != -1) {
		sf::Vector2i temp = sf::Mouse::getPosition();
		inventory[activeIndex]->setPosition(temp.x, temp.y);
		window.draw(*inventory[activeIndex]);
	}
}