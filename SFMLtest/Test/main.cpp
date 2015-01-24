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
# define M_PI 3.14159265358979323846
const sf::Vector2f getVector(float xfinal, float yfinal, float xstart, float ystart, float scale = 1.0f)
{
	float xdir = xfinal - xstart;
	float ydir = yfinal - ystart;
	float len = sqrtf((powf(xdir, 2.0) + powf(ydir, 2.0)));
	return sf::Vector2f(xdir*scale / len, ydir*scale / len);
}
//Grid
std::vector<sf::Drawable*> grid;
bool collision(int x,int y)
{

}
int main()
{
	//Create window

	sf::RenderWindow* window(new sf::RenderWindow(sf::VideoMode(WINDOW_X, WINDOW_Y), "SFML works!"));
	//Load textures
	sf::Texture charac;
	sf::Texture box;
	sf::Texture obstacle;
	if (!charac.loadFromFile("Images/charSq.png"))
	{
		std::cerr << "Could not load char" << std::endl;
	}
	if (!box.loadFromFile("Images/box.png"))
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
	Character spriteChar(charac);
	//Bullets
	std::vector<Bullet*>bullets;
	bullets.reserve(99);
	//Obstacles
	std::vector<Obstacle*>obstacles;
	obstacles.reserve(10);
	for (int x = 0; x < 10; x++)
	{
		int y = rand() % 100;
		grid[y] = new Obstacle(obstacle,64*(y/10),64*(y%10),false);
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
					bullets.push_back(new Bullet(getVector(pressed.x, pressed.y, spriteChar.getPosition().x, spriteChar.getPosition().y, 20), box, spriteChar.getPosition()));
				}
			}
		}
		if (sf::Mouse::isButtonPressed(sf::Mouse::Left))
		{
			localPosition = sf::Mouse::getPosition(*window);
		}
		if (sf::Mouse::isButtonPressed(sf::Mouse::Right))
		{
			/*
			sf::Vector2i pressed = sf::Mouse::getPosition(*window);
			bullets.push_back(Bullet(getVector(pressed.x, pressed.y, spriteChar.getPosition().x, spriteChar.getPosition().y), box, spriteChar.getPosition()));
			*/

		}
		sf::Vector2f spritePosition = spriteChar.getPosition();
		std::cout << spritePosition.x / 64 << " " << spritePosition.y / 64 << std::endl;
		sf::Vector2i mousePosition = sf::Mouse::getPosition(*window);
		spriteChar.setRotation(atan2(mousePosition.y - spritePosition.y, mousePosition.x - spritePosition.x) * 180 / M_PI);
		float len = sqrtf((powf(localPosition.x - spritePosition.x, 2.0) + powf(localPosition.y - spritePosition.y, 2.0)));
		if (len > 1)
		{

				spriteChar.move(getVector(localPosition.x, localPosition.y, spritePosition.x, spritePosition.y));
				/*
				if (spriteChar.getGlobalBounds().intersects(obstacles[0]->getGlobalBounds()))
				{
					spriteChar.move(getVector(localPosition.x, localPosition.y, spritePosition.x, spritePosition.y, -1.0));
					localPosition = sf::Vector2i(spritePosition.x, spritePosition.y);
					spriteChar.stop();
				}*/
		}
		else
		{
			spriteChar.setPosition(localPosition.x, localPosition.y);
		}
		window->clear();
		window->draw(spriteChar);/*
		for (int x = 0; x < bullets.size(); x++)
		{
			if (bullets[x]->getDone())
			{
				Bullet* temp = bullets[x];
				if (bullets.size()-1!=x)
					bullets[x] = std::move(bullets.back());
				bullets.pop_back();
				delete temp;
			}
			else
			{
				bullets[x]->fly();
				for (int x = 0; x < 10; x++)
				{
					for (int y = 0; y < 10; y++)
					{
						if (grid[x][y][0]&&bullets[x]->getGlobalBounds().intersects(obstacles[0]->getGlobalBounds()))
						{
							bullets[x]->setDone(true);
						}
					}
				}
				if (!bullets[x]->getDone())
					window->draw(*bullets[x]);
			}
		}*/
		//velocity.x = localPosition - s;
		//sprite.move(velocity);
		for (int x = 0; x < grid.size(); x++)
		{
			if (grid[x])
				window->draw(*grid[x]);
		}
		window->display();
		elapsed = clock.restart();
	}
	for (int x = 0; x < grid.size(); x++)
		delete grid[x];
	delete window;
	return 0;
}