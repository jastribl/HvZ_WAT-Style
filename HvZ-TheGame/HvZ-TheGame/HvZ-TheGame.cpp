#include "stdafx.h"
#include "Constants.h"
#include "Character.h"
#include "Hud.h"
#include "TextureManager.h"
#include "WorldManager.h"

void updateGame() {
}

int main()
{
	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::Fullscreen, settings));
	sf::View hudView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	sf::View worldView(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));

	Hud hud = Hud();

	TextureManager textureManager = TextureManager();

	WorldManager worldManager = WorldManager(textureManager);

	//Character character = Character(Point(10, 10), textureManager.getTextureFor(CHARACTER, 0), Point(0, 0), 1);


	worldView.move(-1000, 50);

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
					worldManager.moveCHaracter(0, -1);
				}
				else if (event.key.code == sf::Keyboard::Down){
					worldManager.moveCHaracter(0, 1);
				}
				else if (event.key.code == sf::Keyboard::Left){
					worldManager.moveCHaracter(-1, 0);
				}
				else if (event.key.code == sf::Keyboard::Right){
					worldManager.moveCHaracter(1, 0);
				}
				else if (event.key.code == sf::Keyboard::W){
					worldManager.nextWorld();
				}
				break;

			case sf::Event::MouseButtonPressed: {
				Point point = screenToGrid(window.mapPixelToCoords(sf::Mouse::getPosition(window)));
				if (worldManager.getCurrentWorld().getLevel(0).blockExitsAt(point))
				{
					worldManager.getCurrentWorld().getLevel(0).removeBlockAt(point);
				}
				break;
			}

			case sf::Event::Closed:
				window.close();
				break;

			case sf::Event::MouseWheelMoved: {
				if (event.mouseWheel.delta > 0){
					worldView.zoom(0.8);
				}
				else{
					worldView.zoom(1.25);
				}
				break;
			}

			default:
				break;
			}
		}
		for (; elapsedTime >= 0.025f; elapsedTime -= 0.025f) {
			updateGame();
		}
		window.clear(sf::Color(255, 255, 255));

		window.setView(worldView);
		worldManager.getCurrentWorld().draw(window);

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