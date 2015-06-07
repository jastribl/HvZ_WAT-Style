#include "stdafx.h"
#include "WorldManager.h"
#include <fstream>

WorldManager::WorldManager() {}

WorldManager::WorldManager(TextureManager& textureManager) {
	std::ifstream  worldsReader("Resources/Worlds/Worlds.Worlds");
	std::string worldName;
	while (worldsReader >> worldName){
		World world = World();
		std::ifstream  worldReader("Resources/Worlds/" + worldName + ".World");
		int numberOfLevels;
		worldReader >> numberOfLevels;
		for (int i = 0; i < numberOfLevels; i++){
			int numberOfBlocks;
			Level level = Level();
			worldReader >> numberOfBlocks;
			for (int j = 0; j < numberOfBlocks; j++){
				int group, type, x, y;
				worldReader >> group >> type >> x >> y;
				Block block = Block(type, Point(x, y), i, textureManager.getTextureFor(group, type));
				level.addBlockAt(block);
			}
			world.addLevel(level);
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
	if (it == worlds.end()) {
		currentWorld = worlds.begin()->first;
	}
	else {
		currentWorld = std::next(worlds.find(currentWorld))->first;
	}
}