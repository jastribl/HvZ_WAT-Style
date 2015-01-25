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
#include "Point.h"
//Grid
std::vector<Object*> grid;
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
void updateCharOld()
{
	/*
	sf::Vector2i pressed = sf::Mouse::getPosition(*window);
	bullets.push_back(new Bullet(getDirectionVector((float)pressed.x, (float)pressed.y, spriteChar->getPosition().x, spriteChar->getPosition().y, 20.0), bullet, spriteChar->getPosition(), spriteChar->getRotation()));

	localPosition = sf::Mouse::getPosition(*window);
	sf::Vector2f spritePosition = spriteChar->getPosition();
	spriteChar->setVelocity(getDirectionVector((float)localPosition.x, (float)localPosition.y, spritePosition.x, spritePosition.y, 5.0));

	sf::Vector2f spritePosition = spriteChar->getPosition();
	sf::Vector2i mousePosition = sf::Mouse::getPosition(*window);
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
	*/
}
int main()
{
	srand((size_t)(time(NULL)));
	//Create window
	sf::RenderWindow* window(new sf::RenderWindow(sf::VideoMode(WINDOW_X, WINDOW_Y), "HvZ: Wat-Style"));
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
	sf::Texture grass;
	if (!charac.loadFromFile("Images/tallChar.png"))
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
	if (!grass.loadFromFile("Images/grass.png"))
	{
		std::cerr << "Could not load box" << std::endl;
	}
	//Grid
	grid.resize(GRID_X*GRID_Y);
	//Character
	Character* spriteChar = new Character(1,sf::Vector2f(0.0f, 0.0f), charac, 0, 0,false);
	//Bullets
	std::vector<Bullet*>bullets;
	bullets.reserve(99);
	//Obstacles
	for (int num = 0; num < GRID_X*GRID_Y; num++)
	{
		//int ran = rand() % (GRID_X*GRID_Y);
		int ran = num;
		if (!grid[ran])
		{
			int x = (ran / GRID_Y);
			int y = (ran % GRID_Y);
			Point iso=iso2car(Point{ x, y });
			if (rand() % 2==0)
				grid[ran] = new Obstacle(2,obstacle, iso.x, iso.y, false);
			else
				grid[ran] = new Obstacle(3,grass, iso.x, iso.y, false);
			grid[ran]->setPos(Point{x,y});
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
		sf::Vector2f spritePosition = spriteChar->getPosition();
		sf::Vector2i mousePosition = sf::Mouse::getPosition(*window);
		std::ostringstream ss; //string buffer to convert numbers to string
		//ss << (((mousePosition.x - WINDOW_X_HALF - TILE_X) / TILE_X + mousePosition.y / TILE_Y) / 2.0) << " " << ((mousePosition.y / TILE_Y - (mousePosition.x - WINDOW_X_HALF - TILE_X) / TILE_X) / 2.0);// put float into string buffer
		Point iso = car2iso(Point{mousePosition.x,mousePosition.y});
		iso.x = (int)iso.x;
		iso.y = (int)iso.y;
		ss << iso.x << " " << iso.y<<" ";
		if (iso.x >= 0 && iso.x < GRID_X && iso.y>= 0 && iso.y < GRID_Y)
			ss << iso.x*GRID_Y + iso.y << " " << grid[iso.x*GRID_Y + iso.y]->getID();
		text.setString(ss.str());
		while (window->pollEvent(event))
		{
			if (event.type == sf::Event::Closed)
				window->close();
			if (event.type == sf::Event::MouseButtonPressed)
			{
				if (event.mouseButton.button == sf::Mouse::Left)
				{
					//std::cout << iso.x*GRID_Y + iso.y << " " << grid[iso.x*GRID_Y + iso.y]->getID()<<std::endl;
					if (!(iso.x >= GRID_X || iso.x < 0 || iso.y >= GRID_Y || iso.y < 0)&&grid[iso.x*GRID_Y+iso.y]->getID()!=2)
					{
						spriteChar->setPos(iso);
					}
				}
			}
			if (event.type == sf::Event::KeyPressed)
			{
				Point temp = spriteChar->getPos();
				if (event.key.code == sf::Keyboard::Up)
				{
					if ((temp.x != 0) && (grid[(temp.x - 1)*GRID_Y + temp.y]->getID() != 2))
					{
						spriteChar->setPos(Point{temp.x-1,temp.y});
					}
				}
				else if (event.key.code == sf::Keyboard::Down)
				{
					if ((temp.x != GRID_X - 1) && (grid[(temp.x + 1)*GRID_Y + temp.y]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x + 1, temp.y });
					}
				}
				else if (event.key.code == sf::Keyboard::Left)
				{
					if ((temp.y != GRID_Y - 1) && (grid[temp.x*GRID_Y + temp.y + 1]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x, temp.y+1 });
					}
				}
				else if (event.key.code == sf::Keyboard::Right)
				{
					if ((temp.y != 0) && (grid[temp.x*GRID_Y + temp.y - 1]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x, temp.y -1});
					}
				}
			}
		}
		window->clear();
		bool drawChar = false;
		Point car = iso2car(spriteChar->getPos());
		spriteChar->setPosition(car.x, car.y - CHAR_Y + 3 * TILE_Y);
		for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
		{
			if (grid[x])
			{
				window->draw(*grid[x]);
				if (!drawChar && (grid[x]->getPos().x == spriteChar->getPos().x) && (grid[x]->getPos().y == spriteChar->getPos().y))
				{
					window->draw(*spriteChar);
					drawChar = true;
				}
			}
		}
		if (!drawChar)
			window->draw(*spriteChar);
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