#pragma once
#include <map>

typedef std::map<int, std::map<int, sf::Texture>> TextureMap;

class TextureManager {

private:
	TextureMap blockTextures;

public:
	TextureManager();
	~TextureManager();

	void addTextureFor(sf::Texture texture, int group, int type);
	const sf::Texture& getTextureFor(int group, int type);
};