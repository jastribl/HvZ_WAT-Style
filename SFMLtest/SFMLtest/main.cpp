#include <SFML/Graphics.hpp>
#include <SFML/System.hpp>
#include <iostream>
#include <cmath>
#include <vector>
#include <stdlib.h>
#include "Bullet.h"
#include "Obstacle.h"
#include "Character.h"
#include "Constants.h"
#include "Algorithms.h"
//Grid
std::vector<sf::Sprite*> grid;
bool collision(const sf::Sprite& object)
{
	for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
	{
		if (grid[x])
		{
			if (object.getGlobalBounds().intersects(grid[x]->getGlobalBounds()))
			{
				return true;
			}
		}
	}
	return false;
}
int main()
{
	srand((size_t)(time(NULL)));
	//Create window
	sf::RenderWindow* window(new sf::RenderWindow(sf::VideoMode(WINDOW_X, WINDOW_Y), "SFML works!"));
	//Load textures
	sf::Texture charac;
	sf::Texture bullet;
	sf::Texture obstacle;
	if (!charac.loadFromFile("Images/charSq.png"))
	{
		std::cerr << "Could not load char" << std::endl;
	}
	if (!bullet.loadFromFile("Images/bullet.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	if (!obstacle.loadFromFile("Images/full.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	//Grid
	grid.resize(100);
	//Character
	Character* spriteChar = new Character(sf::Vector2f(0.0f, 0.0f), charac, 1000, 300);
	//Bullets
	std::vector<Bullet*>bullets;
	bullets.reserve(99);
	//Obstacles
	for (int x = 0; x < 10; x++)
	{
		int y = rand() % 100;
		if (!grid[y])
			grid[y] = new Obstacle(obstacle, (float)(64.0 * (y / 10.0)), float(64.0 * (y % 10)), false);
	}
	//Remember where you clicked
	sf::Vector2i localPosition = sf::Mouse::getPosition(*window);
	//Set up time
	sf::Clock clock;
	sf::Time elapsed;
	window->setFramerateLimit(FRAMERATE_LIMIT);
	//Main loop
	while (window->isOpen())
	{
		float frame = (1.0f / elapsed.asSeconds());
		//std::cout << frame << std::endl;
		//std::cout << bullets.size() << std::endl;
		sf::Event event;
		while (window->pollEvent(event))
		{
			if (event.type == sf::Event::Closed)
				window->close();
			if (event.type == sf::Event::MouseButtonPressed)
			{
				if (event.mouseButton.button == sf::Mouse::Right)
				{
					sf::Vector2i pressed = sf::Mouse::getPosition(*window);
					bullets.push_back(new Bullet(getDirectionVector((float)pressed.x, (float)pressed.y, spriteChar->getPosition().x, spriteChar->getPosition().y, 20.0), bullet, spriteChar->getPosition(), spriteChar->getRotation()));
				}
			}
		}
		if (sf::Mouse::isButtonPressed(sf::Mouse::Left))
		{
			localPosition = sf::Mouse::getPosition(*window);
			sf::Vector2f spritePosition = spriteChar->getPosition();
			spriteChar->setVelocity(getDirectionVector((float)localPosition.x, (float)localPosition.y, spritePosition.x, spritePosition.y, 5.0));
		}
		if (sf::Mouse::isButtonPressed(sf::Mouse::Right))
		{
			sf::Vector2i pressed = sf::Mouse::getPosition(*window);
			bullets.push_back(new Bullet(getDirectionVector((float)pressed.x, (float)pressed.y, spriteChar->getPosition().x, spriteChar->getPosition().y, 20.0), bullet, spriteChar->getPosition(), spriteChar->getRotation()));
			
		}
		sf::Vector2f spritePosition = spriteChar->getPosition();
		sf::Vector2i mousePosition = sf::Mouse::getPosition(*window);
		float preangle = spriteChar->getRotation();
		float angle = getDirectionAngle((float)mousePosition.x, (float)mousePosition.y, spritePosition.x, spritePosition.y);
		spriteChar->setRotation(angle);
		if (collision(*spriteChar))
		{
			spriteChar->setRotation(preangle);
		}
		if (spriteChar->getVelocity() != sf::Vector2f(0.0f, 0.0f))
		{
			float len = getDistance((float)localPosition.x, (float)localPosition.y, spritePosition.x, spritePosition.y);
			if (len > 3.0)
			{
				//std::cout << "stutter" << std::endl;
				spriteChar->fly();
				if (collision(*spriteChar))
				{
					spriteChar->move(spriteChar->getVelocity()*-1.0f);
					localPosition = sf::Vector2i((int)spritePosition.x, (int)spritePosition.y);
					spriteChar->stop();
				}
			}
			else
			{
				localPosition = sf::Vector2i((int)spriteChar->getPosition().x, (int)spriteChar->getPosition().y);
				spriteChar->stop();

			}
		}
		window->clear();
		window->draw(*spriteChar);
		for (size_t x = 0, xlen = bullets.size(); x < xlen; x++)
		{
			if (bullets[x]->getDone())
			{
				Bullet* temp = bullets[x];
				if (bullets.size() - 1 != x)
				{
					bullets[x] = std::move(bullets.back());
					x--;
				}
				xlen--;
				bullets.pop_back();
				delete temp;
				
			}
			else
			{
				bullets[x]->fly();
				if (collision(*bullets[x]))
					bullets[x]->setDone(true);
				//if (!bullets[x]->getDone())
					window->draw(*bullets[x]);
			}
		}
		//velocity.x = localPosition - s;
		//sprite.move(velocity);
		for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
		{
			if (grid[x])
				window->draw(*grid[x]);
		}
		window->display();
		elapsed = clock.restart();
	}
	delete spriteChar;
	for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
		delete grid[x];
	for (size_t x = 0, xlen = bullets.size(); x < xlen; x++)
		delete bullets[x];
	delete window;
	return 0;
}