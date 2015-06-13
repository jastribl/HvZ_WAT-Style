#pragma once
#include "World.h"
#include "TextureManager.h"
#include "Character.h"
#include <map>
#include <string>

class WorldManager {

private:
	std::map < std::string, World > worlds;
	std::string currentWorld;

public:
	WorldManager(TextureManager& textureManager);
	~WorldManager();

	World& getCurrentWorld();
	void nextWorld();
};