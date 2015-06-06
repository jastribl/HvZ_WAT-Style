#include "stdafx.h"
#include "Constants.h"
#include "Hud.h"
#include <iostream>

void updateGame() {

}

int main()
{
	sf::RenderWindow window(sf::RenderWindow(sf::VideoMode(SCREEN_SIZE_X, SCREEN_SIZE_Y), "HvZ - The Game", sf::Style::None));
	sf::View view(sf::FloatRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y));
	Hud hud = Hud();
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
		window.draw(hud.drawHud());
		window.display();
		elapsedTime += clock.restart().asSeconds();
	}
	//delete window;
	//delete hud;
	return 0;
}