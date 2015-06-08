#include "stdafx.h"
#include "Constants.h"
#include "TextureManager.h"
#include <string>

TextureManager::TextureManager() {
	for (int i = 0; i < numberOfBlocks; i++){
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/block") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, BLOCK, i);
	}
	for (int i = 0; i < numberOfSpecials; i++){
		sf::Texture texture;
		texture.setSmooth(true);
		texture.loadFromFile(std::string("Resources/Images/special") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, SPECIAL, i);
	}
	sf::Texture texture;
	texture.loadFromFile("Resources/Images/character.png");
	addTextureFor(texture, CHARACTER, 0);
}

TextureManager::~TextureManager() {}

void TextureManager::addTextureFor(sf::Texture texture, int group, int type) {
	blockTextures[group][type] = texture;
}

const sf::Texture& TextureManager::getTextureFor(int group, int type) {
	return blockTextures[group][type];
}