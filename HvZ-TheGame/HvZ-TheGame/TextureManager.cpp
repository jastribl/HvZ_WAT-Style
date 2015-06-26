#include "stdafx.h"
#include "TextureManager.h"
#include "Constants.h"
#include <string>

TextureManager::TextureManager() {
	for (int i = 0; i < NUMBER_OF_BLOCK_TYPES; ++i) {
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/Blocks/block") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, BLOCK, i);
	}
	for (int i = 0; i < NUMBER_OF_SPECIAL_TYPES; ++i) {
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/Specials/special") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, SPECIAL, i);
	}
	for (int i = 0; i < NUMBER_OF_BULLET_TYPES; ++i) {
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/Bullets/bullet") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, BULLET, i);
	}
	for (int i = 0; i < NUMBER_OF_INVENTORY_ITEMS; ++i) {
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/Inventory/inventory") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, INVENTORY_ITEM, i);
	}
	for (int i = NUMBER_OF_INVENTORY_ITEMS; i < BOX_NUM; ++i) {
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/Inventory/inventory8.png"));
		addTextureFor(texture, INVENTORY_ITEM, i);
	}
	sf::Texture characterTexture;
	characterTexture.loadFromFile("Resources/Images/Character/character.png");
	addTextureFor(characterTexture, CHARACTER, 0);
}

TextureManager::~TextureManager() {}

void TextureManager::addTextureFor(sf::Texture texture, int group, int type) {
	blockTextures[group][type] = texture;
}

const sf::Texture& TextureManager::getTextureFor(int group, int type) {
	return blockTextures[group][type];
}