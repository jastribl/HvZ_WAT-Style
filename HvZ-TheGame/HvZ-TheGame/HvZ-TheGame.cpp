#include "stdafx.h"
#include "Constants.h"
#include "WorldManager.h"
#include "TextureManager.h"
#include "Hud.h"
#include "World.h"
#include "Character.h"

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
	BaseClass* character = new Character(*worldManager.getCurrentWorld(), textureManager.getTextureFor(CHARACTER, 0), Point(1, 1, 1));

	worldView.move(-1000, 50);

	window.setFramerateLimit(10);
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
					worldManager.nextWorld();
				}
				break;

				//case sf::Event::MouseButtonPressed: {
				//	Point point = screenToGrid(window.mapPixelToCoords(sf::Mouse::getPosition(window)), character->gridLocation.z);
				//	if (!worldManager.getCurrentWorld()->existsAt(point)) {
				//		character->setStageLocation(point);
				//	}
				//	break;
				//}

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