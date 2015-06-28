#include "stdafx.h"
#include "WorldManager.h"
#include "TextureManager.h"
#include "Hud.h"
#include "World.h"
#include "Character.h"
#include "Bullet.h"
#define _USE_MATH_DEFINES
#include <math.h>

//void updateGame() {}

int main() {
	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::Fullscreen, settings));
	sf::View hudView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	sf::View worldView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));


	TextureManager textureManager = TextureManager();
	Hud hud = Hud(textureManager);
	WorldManager worldManager = WorldManager(textureManager);
	Character* character = new Character(worldManager.getCurrentWorld(), textureManager.getTextureFor(CHARACTER, 0), sf::Vector3i(3, 3, 1), sf::Vector3f(0, 0, 0));

	window.setFramerateLimit(60);
	//sf::Clock clock;
	//float elapsedTime = 0.0f;
	int zoomLevel = 0;
	while (window.isOpen()) {
		sf::Event event;
		while (window.pollEvent(event)) {
			switch (event.type) {
				case sf::Event::KeyPressed:
					if (event.key.code == sf::Keyboard::Escape) {
						window.close();
					} else if (event.key.code == sf::Keyboard::W) {
						worldManager.getCurrentWorld().removeItemFromWorld(character);
						worldManager.goToNextWorld();
						worldManager.getCurrentWorld().add(character);
					}
					break;

				case sf::Event::Closed:
					window.close();
					break;

				case sf::Event::MouseWheelMoved:
					if (event.mouseWheel.delta > 0) {
						if (zoomLevel > -2) {
							worldView.zoom(0.9f);
							zoomLevel--;
						}
					} else {
						if (zoomLevel < 6) {
							worldView.zoom(1.1f);
							zoomLevel++;
						}
					}
					break;

				case sf::Event::MouseButtonPressed:
					if (event.mouseButton.button == sf::Mouse::Left) {
						if (event.mouseButton.x > SIDEBAR_ORIGIN_X && event.mouseButton.y > SIDEBAR_ORIGIN_Y) {
							hud.click(event.mouseButton.x, event.mouseButton.y);
						} else {
							character->setDestination(sf::Vector3f(isometricToCartesian(window.mapPixelToCoords(sf::Mouse::getPosition(window)), 0)));
						}
					}
					break;

				case sf::Event::MouseButtonReleased:
					if (event.mouseButton.button == sf::Mouse::Left) {
						hud.release(event.mouseButton.x, event.mouseButton.y);
					}
					break;
			}
		}


		if (sf::Mouse::isButtonPressed(sf::Mouse::Right)) {
			Location bulletLocation = character->loc;
			bulletLocation.add(0, 0, HALF_BLOCK_SIZE);
			sf::Vector3f mousePosition = sf::Vector3f(isometricToCartesian(window.mapPixelToCoords(sf::Mouse::getPosition(window)), 0));
			float angle = (std::atan2(((bulletLocation.getGrid().y * -BLOCK_SIZE) - bulletLocation.getPoint().y) + mousePosition.y, (bulletLocation.getGrid().x * -BLOCK_SIZE - bulletLocation.getPoint().x) + mousePosition.x));
			BaseClass* bullet = new Bullet(worldManager.getCurrentWorld(), textureManager.getTextureFor(BULLET, 0), bulletLocation.getGrid(), bulletLocation.getPoint(), 20, angle);
		}

		sf::Vector2i mouseWindowPosition = sf::Mouse::getPosition(window);
		if (mouseWindowPosition.x > window.getSize().x - 10) {
			worldView.move(20, 0);
		} else if (mouseWindowPosition.x < 10) {
			worldView.move(-20, 0);
		} else if (mouseWindowPosition.y > window.getSize().y - 10) {
			worldView.move(0, 20);
		} else if (mouseWindowPosition.y < 10) {
			worldView.move(0, -20);
		}

		//for (; elapsedTime >= 0.025f; elapsedTime -= 0.025f) {
		//	updateGame();
		//}

		window.clear(sf::Color(255, 255, 255));

		window.setView(worldView);
		worldManager.getCurrentWorld().updateAndDraw(window);

		window.setView(hudView);
		//hud.setHP(rand() % 101);
		//hud.setMP(rand() % 101);
		hud.drawToWindow(window);

		window.setView(worldView); //view need to be set this way to ensure the hud doesn't move, and the blokcs can move
		window.display();
		//elapsedTime += clock.restart().asSeconds();
	}
	return 0;
}