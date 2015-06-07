#include "stdafx.h"
#include "Constants.h"
#include "Hud.h"
#include "World.h"
#include "Level.h"
#include "Block.h"
#include "TextureManager.h"
#include "WorldManager.h"
#include <fstream>

void updateGame() {
}

int main()
{
	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::None, settings));
	sf::View hudView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	sf::View worldView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));

	Hud hud = Hud();

	TextureManager textureManager = TextureManager();

	WorldManager worldManager = WorldManager(textureManager);

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
				else if (event.key.code == sf::Keyboard::Up){
					worldView.move(0, -10);
				}
				else if (event.key.code == sf::Keyboard::Down){
					worldView.move(0, 10);
				}
				else if (event.key.code == sf::Keyboard::Left){
					worldView.move(-10., 0);
				}
				else if (event.key.code == sf::Keyboard::Right){
					worldView.move(10, 0);
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
		window.setView(hudView);
		hud.setHP(rand() % 101);
		hud.setMP(rand() % 101);
		hud.drawToWindow(window);
		window.setView(worldView);
		worldManager.getCurrentWorld().draw(window);
		window.display();
		elapsedTime += clock.restart().asSeconds();
	}
	return 0;
}