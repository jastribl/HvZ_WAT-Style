#include <SFML/Graphics.hpp>
#include <SFML/System.hpp>
#include <iostream>
#include <sstream>
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
bool collision(const sf::Sprite& object, bool check)
{
	for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
	{
		if (grid[x])
		{
			if (check)
			{
				if (object.getGlobalBounds().intersects(grid[x]->getGlobalBounds()))
				{
					return true;
				}
			}
			else
			{
				if (grid[x]->getGlobalBounds().contains(object.getPosition()))
				{
					return true;
				}
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
	//Load textures and Fonts
	sf::Font font;
	if (!font.loadFromFile("arial.ttf"))
	{
		std::cerr << "Could not load font" << std::endl;
	}
	sf::Text text;
	text.setFont(font);
	text.setString("Hello world");
	text.setCharacterSize(24);
	text.setColor(sf::Color::Red);
	text.setStyle(sf::Text::Bold | sf::Text::Underlined);
	text.setPosition(0.0f, 0.0f);
	sf::Texture charac;
	sf::Texture bullet;
	sf::Texture obstacle;
	if (!charac.loadFromFile("Images/charSq.png"))
	{
		std::cerr << "Could not load char" << std::endl;
	}
	if (!bullet.loadFromFile("Images/bullet.png"))
	{
		std::cerr << "Could not load bullet" << std::endl;
	}
	if (!obstacle.loadFromFile("Images/box.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	//Grid
	grid.resize(GRID_X*GRID_Y);
	//Character
	Character* spriteChar = new Character(sf::Vector2f(0.0f, 0.0f), charac, 1000, 300);
	//Bullets
	std::vector<Bullet*>bullets;
	bullets.reserve(99);
	//Obstacles
	for (int num = 0; num < GRID_X*GRID_Y; num++)
	{
		int ran = rand() % (GRID_X*GRID_Y);

		if (!grid[ran])
		{
			int x = (ran / GRID_X);
			int y = (ran % GRID_Y);
			std::cout << ran<<" "<<x << " " << y << std::endl;
			float isox = x - y;
			float isoy = x + y;
			std::cout << isox << " " << isoy << std::endl;
			grid[ran] = new Obstacle(obstacle, (float)(isox*TILE_X + 500), (float)(isoy*TILE_Y), false);
			//grid[y] = new Obstacle(obstacle, (float)(64.0 * (y / 10.0)), float(64.0 * (y % 10)), false);
		}
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
		//Calculate framerate
		//float frame = (1.0f / elapsed.asSeconds());
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
		std::ostringstream ss; //string buffer to convert numbers to string
		ss << (((mousePosition.x-500.0) / TILE_X + mousePosition.y / TILE_Y) / 2.0) << " " << ((mousePosition.y / TILE_Y - (mousePosition.x-500.0) / TILE_X) / 2);// put float into string buffer
		text.setString(ss.str());
		float preangle = spriteChar->getRotation();
		float angle = getDirectionAngle((float)mousePosition.x, (float)mousePosition.y, spritePosition.x, spritePosition.y);
		spriteChar->setRotation(angle);
		if (collision(*spriteChar, true))
		{
			spriteChar->setRotation(preangle);
		}
		if (spriteChar->getVelocity() != sf::Vector2f(0.0f, 0.0f))
		{
			float len = getDistance((float)localPosition.x, (float)localPosition.y, spritePosition.x, spritePosition.y);
			if (len > 3.0)
			{
				spriteChar->fly();
				if (collision(*spriteChar, true))
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
				if (collision(*bullets[x], false))
					bullets[x]->setDone(true);
				if (!bullets[x]->getDone())
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
		window->draw(text);
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