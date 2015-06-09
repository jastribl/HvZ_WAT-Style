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
	Character character;
	WorldManager(TextureManager& textureManager);
	~WorldManager();
	World& getCurrentWorld();
	void nextWorld();
	void moveCHaracter(int x, int y);
};