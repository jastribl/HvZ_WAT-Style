#include "stdafx.h"
#include "WorldManager.h"
#include "Constants.h"
#include "Block.h"
#include "Point.h"
#include <fstream>

WorldManager::WorldManager(TextureManager& textureManager)
	:character(Point(3, 3), textureManager.getTextureFor(CHARACTER, 0), 1) {
	std::ifstream  worldsReader("Resources/Worlds/Worlds.Worlds");
	std::string worldName;
	while (worldsReader >> worldName) {
		World world = World();
		std::ifstream  worldReader("Resources/Worlds/" + worldName + ".World");
		int numberOfLevels;
		worldReader >> numberOfLevels;
		for (int i = 0; i < numberOfLevels; i++) {
			int numberOfBlocks;
			Level level = Level();
			worldReader >> numberOfBlocks;
			for (int j = 0; j < numberOfBlocks; j++){
				int group, type, x, y;
				worldReader >> group >> type >> x >> y;
				Block block = Block(Point(x, y), textureManager.getTextureFor(group, type), type, i);
				level.addBlock(block);
			}
			if (i == character.level){
				level.addBlock(character);
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
	if (it == std::prev(worlds.end())) {
		currentWorld = worlds.begin()->first;
	}
	else {
		currentWorld = std::next(worlds.find(currentWorld))->first;
	}
}

void WorldManager::moveCHaracter(int x, int y) {
	character.move(x, y);
	if (getCurrentWorld().getLevel(character.level).blockExitsAt(character.gridDestination)) {
		character.stop();
	}
	else{
		getCurrentWorld().getLevel(character.level).removeBlockAt(character.gridLocation);
		character.applyMove();
		getCurrentWorld().getLevel(character.level).addBlock(character);

	}
}