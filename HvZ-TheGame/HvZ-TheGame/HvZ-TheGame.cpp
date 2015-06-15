#include "stdafx.h"
#include "Constants.h"
#include "WorldManager.h"
#include "TextureManager.h"
#include "Hud.h"
#include "World.h"
#include "Character.h"
#include <iostream>

//void updateGame() {}

int main() {
	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::Fullscreen, settings));
	sf::View hudView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	sf::View worldView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));


	Hud hud = Hud();
	TextureManager textureManager = TextureManager();
	WorldManager worldManager = WorldManager(textureManager);
	BaseClass* character = new Character(*worldManager.getCurrentWorld(), textureManager.getTextureFor(CHARACTER, 0), Point(1, 1, 1), Point(0, 0, 0));

	window.setFramerateLimit(60);
	sf::Clock clock;
	float elapsedTime = 0.0f;
	while (window.isOpen()) {
		sf::Event event;
		while (window.pollEvent(event)) {
			switch (event.type) 			{
			case sf::Event::KeyPressed:
				if (event.key.code == sf::Keyboard::Escape){
					window.close();
				}
				else if (event.key.code == sf::Keyboard::Up){
					character->move(0, -1, 0);
				}
				else if (event.key.code == sf::Keyboard::Down){
					character->move(0, 1, 0);
				}
				else if (event.key.code == sf::Keyboard::Left){
					character->move(-1, 0, 0);
				}
				else if (event.key.code == sf::Keyboard::Right){
					character->move(1, 0, 0);
				}
				else if (event.key.code == sf::Keyboard::PageUp){
					character->move(0, 0, 1);
				}
				else if (event.key.code == sf::Keyboard::PageDown){
					character->move(0, 0, -1);
				}
				else if (event.key.code == sf::Keyboard::W){
					worldManager.getCurrentWorld()->removeFromMap(character->gridLocation, character->pointLocation);
					worldManager.nextWorld();
					worldManager.getCurrentWorld()->add(character);
				}
				break;

			case sf::Event::Closed:
				window.close();
				break;

			case sf::Event::MouseWheelMoved: {
				if (event.mouseWheel.delta > 0) {
					worldView.zoom(0.8);
				}
				else {
					worldView.zoom(1.25);
				}
				break;
			}

			default:
				break;
			}
		}

		//Point point = screenToGrid(window.mapPixelToCoords(sf::Mouse::getPosition(window)), 1);
		//if (!worldManager.getCurrentWorld()->itemsExistAt(point)) {
		//	Point p = Point(point.x - character->gridLocation.x, point.y - character->gridLocation.y, 0);
		//	character->move(p.x, p.y, p.z);
		//	//worldManager.getCurrentWorld()->removeAt(point);
		//}



		sf::Vector2i mousePositionWindow = sf::Mouse::getPosition(window);
		if (mousePositionWindow.x > 1366 - 10) {
			worldView.move(20, 0);
		}
		else if (mousePositionWindow.x < 10) {
			worldView.move(-20, 0);
		}
		else if (mousePositionWindow.y > 768 - 10) {
			worldView.move(0, 20);
		}
		else if (mousePositionWindow.y < 10) {
			worldView.move(0, -20);
		}

		//for (; elapsedTime >= 0.025f; elapsedTime -= 0.025f) {
		//	updateGame();
		//}
		window.clear(sf::Color(255, 255, 255));

		window.setView(worldView);
		worldManager.getCurrentWorld()->draw(window);

		window.setView(hudView);
		hud.setHP(rand() % 101);
		hud.setMP(rand() % 101);
		hud.drawToWindow(window);

		window.setView(worldView); //view need to be set this way to ensure the hud doesn't move, and the blokcs can move
		window.display();
		elapsedTime += clock.restart().asSeconds();
	}
	return 0;
}