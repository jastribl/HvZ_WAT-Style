#pragma once
class TextureManager;
#include "World.h"
#include <map>
#include <string>

typedef std::map <std::string, World> Worlds;

class WorldManager {

private:
	Worlds worlds;
	std::string currentWorld;

public:
	WorldManager(TextureManager& textureManager);
	~WorldManager();

	World& getCurrentWorld();
	void goToNextWorld();
};