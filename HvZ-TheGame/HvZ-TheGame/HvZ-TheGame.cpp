#include "stdafx.h"
#include "Constants.h"
#include "Hud.h"
#include "World.h"
#include "Level.h"
#include "Block.h"
#include "TextureManager.h"
#include <fstream>
#include <iostream>
#include <stdlib.h>

void updateGame() {
}

int main()
{
	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::None, settings));
	sf::View view(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));

	Hud hud = Hud();

	TextureManager textureManager = TextureManager();

	World world = World();
	std::ifstream  worldReader("Resources/Maps/the.World");
	int numberOfLevels;
	worldReader >> numberOfLevels;
	for (int i = 0; i < numberOfLevels; i++){
		int numberOfBlocks;
		Level level;
		worldReader >> numberOfBlocks;
		for (int j = 0; j < numberOfBlocks; j++){
			int group, type, x, y;
			worldReader >> group >> type >> x >> y;
			Block block = Block(type, Point(x, y), textureManager.getTextureFor(group, type));
			level.addBlockAt(block);
		}
		world.addLevel(level);
	}

	window.setView(view);
	window.setFramerateLimit(10);
	sf::Clock clock;
	float elapsedTime = 0.0f;
	while (window.isOpen()) {
		sf::Event event;
		while (window.pollEvent(event)) {
			switch (event.type)
			{
			case sf::Event::KeyPressed:
				if (event.key.code == sf::Keyboard::Escape){
					window.close();
				}
				break;

			case sf::Event::Closed:
				window.close();
				break;

			default:
				break;
			}
		}
		for (; elapsedTime >= 0.025f; elapsedTime -= 0.025f) {
			updateGame();
		}
		window.clear(sf::Color(255, 255, 255));
		world.draw(window);
		hud.setHP(rand() % 101);
		hud.setMP(rand() % 101);
		hud.drawToWindow(window);
		window.display();
		elapsedTime += clock.restart().asSeconds();
	}
	return 0;
}