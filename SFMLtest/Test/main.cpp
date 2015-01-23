#include <SFML/Graphics.hpp>
#include <SFML/System.hpp>
#include <iostream>
#include <cmath>
#include <vector>
#include <memory>
#include "Bullet.h"
#include "Obstacle.h"
# define M_PI 3.14159265358979323846
const sf::Vector2f getVector(float xfinal, float yfinal, float xstart, float ystart,float scale=1.0f)
{
	float xdir = xfinal - xstart;
	float ydir = yfinal - ystart;
	float len = sqrtf((powf(xdir, 2.0) + powf(ydir, 2.0)));
	return sf::Vector2f(xdir*scale / len, ydir*scale / len);
}
int main()
{
	//Create window
	
	sf::RenderWindow* window(new sf::RenderWindow(sf::VideoMode(1280, 720), "SFML works!"));
	const float speed = 0.2f;
	//Load textures
	sf::Texture charac;
	sf::Texture box;
	sf::Texture obstacle;
	if (!charac.loadFromFile("char.png"))
	{
		std::cerr << "Could not load char" << std::endl;
	}
	if (!box.loadFromFile("box.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	if (!obstacle.loadFromFile("obstacle.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	//Character
	sf::Sprite spriteChar;
	spriteChar.setTexture(charac);
	spriteChar.setOrigin(charac.getSize().x / 2, charac.getSize().y / 2);
	//Bullets
	std::vector<Bullet>bullets;
	bullets.reserve(99999);
	//Obstacles
	std::vector<Obstacle>obstacles;
	obstacles.reserve(99999);
	obstacles.push_back(Obstacle(obstacle, 500, 500));
	//Remember where you clicked
	sf::Vector2i localPosition = sf::Mouse::getPosition(*window);
	//Set up time
	sf::Clock clock;
	sf::Time elapsed;
	window->setFramerateLimit(200);
	//Main loop
	while (window->isOpen())
	{
		float frame = (1.0f / elapsed.asSeconds());
		//std::cout << frame << std::endl;
		std::cout << bullets.size() << std::endl;
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
					bullets.push_back(Bullet(getVector(pressed.x, pressed.y, spriteChar.getPosition().x, spriteChar.getPosition().y,20), box, spriteChar.getPosition()));
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
		sf::Vector2i mousePosition = sf::Mouse::getPosition(*window);
		spriteChar.setRotation(atan2(mousePosition.y - spritePosition.y, mousePosition.x - spritePosition.x) * 180 / M_PI);
		float len = sqrtf((powf(localPosition.x - spritePosition.x, 2.0) + powf(localPosition.y - spritePosition.y, 2.0)));
		if (len > 1)
		{
			spriteChar.move(getVector(localPosition.x, localPosition.y, spritePosition.x, spritePosition.y));
		}
		else
		{
			spriteChar.setPosition(localPosition.x, localPosition.y);
		}
		window->clear();
		window->draw(spriteChar);
		for (int x = 0; x < bullets.size(); x++)
		{
			if (bullets[x].getPosition().x > 1500)
			{
				
			}
			else
			{
				if (bullets[x].getDone())
				{
					bullets.erase(bullets.begin() + x);
				}
				else
				{
					bullets[x].fly();
					if (bullets[x].getGlobalBounds().intersects(obstacles[0].getGlobalBounds()))
					{
						bullets[x].setDone(true);
					}
					window->draw(bullets[x]);
				}
			}
		}
		//velocity.x = localPosition - s;
		//sprite.move(velocity);
		window->draw(obstacles[0]);
		window->display();
		elapsed = clock.restart();
	}
	delete window;
	return 0;
}