#include "stdafx.h"
#include "TextureManager.h"
#include <string>

TextureManager::TextureManager() {
	for (int i = 0; i < numberOfBlocks; i++){
		sf::Texture texture;
		texture.loadFromFile(std::string("Resources/Images/block") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, 0, i);
	}
	for (int i = 0; i < numberOfSpecials; i++){
		sf::Texture texture;
		texture.loadFromFile(std::string("Resources/Images/special") + std::to_string(i) + std::string(".png"));
		addTextureFor(texture, 1, i);
	}
}

TextureManager::~TextureManager() {}

void TextureManager::addTextureFor(sf::Texture texture, int group, int type){
	textures[group][type] = texture;
}

const sf::Texture& TextureManager::getTextureFor(int group, int type){
	return textures[group][type];
}