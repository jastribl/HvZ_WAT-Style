#include "stdafx.h"
#include "WorldManager.h"
#include "Constants.h"
#include "Block.h"
#include "Point.h"
#include <fstream>

WorldManager::WorldManager(TextureManager& textureManager) {
	std::ifstream  worldsReader("Resources/Worlds/Worlds.Worlds");
	std::string worldName;
	while (worldsReader >> worldName) {
		World world = World();
		std::ifstream  worldReader("Resources/Worlds/" + worldName + ".World");
		int numberOfLevels;
		worldReader >> numberOfLevels;
		for (int z = 0; z < numberOfLevels; z++) {
			int numberOfBlocks;
			worldReader >> numberOfBlocks;
			for (int j = 0; j < numberOfBlocks; j++){
				int group, type, x, y;
				worldReader >> group >> type >> x >> y;
				BaseClass* block = new Block(Point(x, y, z), textureManager.getTextureFor(group, type), type);
				world.add(block);
			}
		}
		worlds[worldName] = world;
	}
	currentWorld = worlds.begin()->first;
}

WorldManager::~WorldManager() {}

World& WorldManager::getCurrentWorld() {
	return worlds.find(currentWorld)->second;
}

void WorldManager::nextWorld() {
	std::map < std::string, World >::iterator it = worlds.find(currentWorld);
	if (it == std::prev(worlds.end())) {
		currentWorld = worlds.begin()->first;
	}
	else {
		currentWorld = std::next(worlds.find(currentWorld))->first;
	}
}