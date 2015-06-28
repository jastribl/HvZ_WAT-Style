#include "stdafx.h"
#include "WorldManager.h"
#include "TextureManager.h"
#include "Block.h"
#include <fstream>

WorldManager::WorldManager(TextureManager& textureManager) {
	std::ifstream  worldsReader("Resources/Worlds/Worlds.Worlds");
	std::string worldName;
	while (worldsReader >> worldName) {
		World world = World(worldName);
		std::ifstream  worldReader("Resources/Worlds/" + worldName + ".World");
		int numberOfLevels;
		worldReader >> numberOfLevels;
		for (int z = 0; z < numberOfLevels; z++) {
			int numberOfBlocks;
			worldReader >> numberOfBlocks;
			for (int j = 0; j < numberOfBlocks; j++) {
				int group, type, x, y;
				worldReader >> group >> type >> x >> y;
				BaseClass* block = new Block(world, textureManager.getTextureFor(group, type), sf::Vector3i(x, y, z), type);
			}
		}
		worlds.insert(std::make_pair(worldName, world));
	}
	currentWorld = worlds.begin()->first;
}

WorldManager::~WorldManager() {}

World& WorldManager::getCurrentWorld() {
	return worlds.find(currentWorld)->second;
}

void WorldManager::goToNextWorld() {
	auto it = worlds.find(currentWorld);
	if (it == std::prev(worlds.end())) {
		currentWorld = worlds.begin()->first;
	} else {
		currentWorld = std::next(worlds.find(currentWorld))->first;
	}
}