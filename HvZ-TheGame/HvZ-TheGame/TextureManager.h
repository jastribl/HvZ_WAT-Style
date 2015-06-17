#pragma once
#include <map>

class TextureManager {

private:
	std::map<int, std::map<int, sf::Texture>> blockTextures;

public:
	TextureManager();
	~TextureManager();

	void addTextureFor(sf::Texture texture, int group, int type);
	const sf::Texture& getTextureFor(int group, int type);
};