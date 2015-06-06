#include "stdafx.h"
#include "Constants.h"
#include "Hud.h"
#include "World.h"
#include "Level.h"
#include "Block.h"
#include <fstream>
#include <iostream>

void updateGame() {
}

int main()
{
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::None));
	sf::View view(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	Hud hud = Hud();


	sf::Texture texture;
	texture.loadFromFile("Resources/Images/block0.png");
	sf::Sprite sprite;
	sprite.setTexture(texture);
	sprite.setPosition(200, 200);

	World world;
	ifstream  worldReader("Resources/Maps/the.World");
	int numberOfLevels;
	worldReader >> numberOfLevels;
	int count = 0;
	for (int i = 0; i < numberOfLevels; i++){
		int numberOfBlocks;
		Level level;
		worldReader >> numberOfBlocks;
		for (int j = 0; j < numberOfBlocks; j++){
			int group, type, x, y;
			worldReader >> group >> type >> x >> y;
			level.addBlockAt(Block(type, Point(x * 8, y * 8), texture));
			count++;
		}
		world.addLevel(level);
	}
	std::cout << count;


	window.setView(view);
	window.setFramerateLimit(60);
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
		hud.drawToWindow(window);
		window.draw(sprite);
		window.display();
		elapsedTime += clock.restart().asSeconds();
	}
	return 0;
}