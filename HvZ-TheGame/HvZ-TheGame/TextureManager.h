#pragma once

#include <map>

class TextureManager {

private:
	int numberOfBlocks = 9;
	int numberOfSpecials = 6;
	std::map<int, std::map<int, sf::Texture>> blockTextures;
	sf::Texture characterTexture;

public:
	TextureManager();
	~TextureManager();

	void addTextureFor(sf::Texture texture, int group, int type);
	const sf::Texture& getTextureFor(int group, int type);
};