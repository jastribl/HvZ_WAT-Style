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
bool collision(Object& object)
{
	Point temp = object.getPos();
	//std::cout << temp.x<<" "<<temp.y << std::endl;
	if (temp.x >= 0 && temp.x < GRID_X&&temp.y >= 0 && temp.y < GRID_Y)
	{
		//std::cout << grid[(int)(temp.x)*GRID_Y + (int)(temp.y)]->getID() << std::endl;
		if (grid[(int)(temp.x)*GRID_Y + (int)(temp.y)]->getID() == 2)
			return true;
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


	*/
}
bool bulletCompare(Object* a, Object* b)
{
	return (a->getPos().x)*GRID_Y + (a->getPos().y) < (b->getPos().x)*GRID_Y + (b->getPos().y);
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
	if (!bullet.loadFromFile("Images/bulletSmall.png"))
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
	Character* spriteChar = new Character(1, sf::Vector2f(0.0f, 0.0f), charac, 0, 0, false);
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
			Point iso = iso2car(Point{ x, y }, 1);
			if (rand() % 10 == 0)
				grid[ran] = new Obstacle(2, obstacle, iso.x, iso.y, false);
			else
				grid[ran] = new Obstacle(3, grass, iso.x, iso.y, false);
			grid[ran]->setPos(Point{ x, y });
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
		Point iso = car2iso(Point{ mousePosition.x, mousePosition.y }, 1);
		iso.x = (int)iso.x;
		iso.y = (int)iso.y;
		ss << iso.x << " " << iso.y << " ";
		if (iso.x >= 0 && iso.x < GRID_X && iso.y >= 0 && iso.y < GRID_Y)
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
					if (!(iso.x >= GRID_X || iso.x < 0 || iso.y >= GRID_Y || iso.y < 0) && grid[iso.x*GRID_Y + iso.y]->getID() != 2)
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
						spriteChar->setPos(Point{ temp.x - 1.0, temp.y });
					}
				}
				else if (event.key.code == sf::Keyboard::Down)
				{
					if ((temp.x != GRID_X - 1) && (grid[(temp.x + 1)*GRID_Y + temp.y]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x + 1.0, temp.y });
					}
				}
				else if (event.key.code == sf::Keyboard::Left)
				{
					if ((temp.y != GRID_Y - 1) && (grid[temp.x*GRID_Y + temp.y + 1]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x, temp.y + 1.0 });
					}
				}
				else if (event.key.code == sf::Keyboard::Right)
				{
					if ((temp.y != 0) && (grid[temp.x*GRID_Y + temp.y - 1]->getID() != 2))
					{
						spriteChar->setPos(Point{ temp.x, temp.y - 1.0 });
					}
				}
			}
			if (event.type == sf::Event::MouseButtonPressed)
			{
				if (event.mouseButton.button == sf::Mouse::Right)
				{
					sf::Vector2i pressed = sf::Mouse::getPosition(*window);
					Point temp = iso2car(spriteChar->getPos(), 0);
					bullets.push_back(new Bullet(4, getDirectionVector((float)pressed.x, (float)pressed.y, (int)temp.x + TILE_X, (int)temp.y + 2 * TILE_Y, 10.0), bullet, (int)temp.x + TILE_X, (int)temp.y + 2 * TILE_Y, spriteChar->getRotation(), true));
				}
			}
		}
		
		if (sf::Mouse::isButtonPressed(sf::Mouse::Right))
		{
			sf::Vector2i pressed = sf::Mouse::getPosition(*window);
			Point temp = iso2car(spriteChar->getPos(), 0);
			bullets.push_back(new Bullet(4, getDirectionVector((float)pressed.x, (float)pressed.y, (int)temp.x + TILE_X, (int)temp.y + 2 * TILE_Y, 10.0), bullet, (int)temp.x + TILE_X, (int)temp.y + 2 * TILE_Y, spriteChar->getRotation(), true));

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
				bullets[x]->setPos(car2iso(Point{ bullets[x]->getPosition().x, bullets[x]->getPosition().y }, 2));
				if (collision(*bullets[x]))
					bullets[x]->setDone(true);
			}
		}
		std::sort(bullets.begin(), bullets.end(), bulletCompare);
		/*for (size_t x = 0, xlen = bullets.size(); x < xlen; x++)
		{
		std::cout << bullets[x]->getPos().x << " ";
		}
		std::cout << std::endl;*/
		window->clear(sf::Color(255, 255, 255));
		spriteChar->setDrawn(false);
		for (size_t x = 0, xlen = bullets.size(); x < xlen; x++)
		{
			bullets[x]->setDrawn(false);
		}
		Point car = iso2car(spriteChar->getPos(), 1);
		spriteChar->setPosition(car.x, car.y - CHAR_Y + 3 * TILE_Y);
		int count = 0;
		int loc = 0;
		for (size_t x = 0, xlen = grid.size(); x < xlen; x++)
		{
			if (grid[x])
			{
				window->draw(*grid[x]);
				if (!(spriteChar->getDrawn()) && (grid[x]->getPos() == spriteChar->getPos()))
				{
					window->draw(*spriteChar);
					spriteChar->setDrawn(true);
				}
				while (count<bullets.size() && !(loc>x)&&(grid[x]->getID() != 2))
				{
					Point temp = bullets[count]->getPos();
					if (!bullets[count]->getDone() && !bullets[count]->getDrawn())
					{
						loc = temp.x*GRID_Y + temp.y;
						//std::cout << loc <<" " <<x<<std::endl;
						if (loc <= x)
						{
							//std::cout << "DRAW" << std::endl;
							window->draw(*bullets[count]);
							bullets[count]->setDrawn(true);
							count++;
						}
						else
						{
							break;
						}
					}
					else
					{
						count++;
					}
				}
			}
		}
		for (size_t x = count, xlen = bullets.size(); x < xlen; x++)
		{
			if (!bullets[x]->getDone() && !bullets[x]->getDrawn())
			{
				//std::cout << "drawing out" << std::endl;
				window->draw(*bullets[x]);
				bullets[x]->setDrawn(true);
			}
		}
		if (!spriteChar->getDrawn())
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