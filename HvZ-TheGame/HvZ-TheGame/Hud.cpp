#include "stdafx.h"
#include "Hud.h"
#include "Constants.h"
#include "Inventory.h"
#include "TextureManager.h"
#include <ctime>
#include <iostream>

Hud::Hud(TextureManager& textureManager)
	: inventory(textureManager) {
	hpFont.loadFromFile("Resources/Fonts/arial.ttf");
	timeFont.loadFromFile("Resources/Fonts/DS-DIGI.ttf");
	static int MINIMAP_SIZE = 120;
	minimap = sf::CircleShape(MINIMAP_SIZE);
	minimap.setFillColor(sf::Color(150, 150, 150));
	minimap.setOutlineColor(sf::Color::Black);
	minimap.setOutlineThickness(5);
	minimap.setOrigin(MINIMAP_SIZE, MINIMAP_SIZE);
	minimap.setPosition(1770, 250);
}

Hud::~Hud() {}

void Hud::setHP(float newhp) {
	hp = newhp;
}

void Hud::setMP(float newmp) {
	mp = newmp;
}
void Hud::click(int x, int y) {
	if (x > BOX_ORIGIN_X&&x<BOX_ORIGIN_X + BOX_SIZE_LENGTH&&y>BOX_ORIGIN_Y&&y < BOX_ORIGIN_Y + BOX_SIZE_WIDTH) {
		inventory.click(x, y);
	}
}
void Hud::release(int x, int y) {
	inventory.release(x, y);
}
void Hud::drawToWindow(sf::RenderWindow& window) {

	std::vector<sf::Vertex> vertices;
	//Sidebar background
	vertices.push_back(sf::Vertex(sf::Vector2f(SIDEBAR_ORIGIN_X, 0), sf::Color(64, 64, 64)));
	vertices.push_back(sf::Vertex(sf::Vector2f(SIDEBAR_ORIGIN_X, SCREEN_SIZE_Y), sf::Color(50, 50, 50)));
	vertices.push_back(sf::Vertex(sf::Vector2f(SIDEBAR_ORIGIN_X + SIDEBAR_LENGTH, SCREEN_SIZE_Y), sf::Color(50, 50, 50)));
	vertices.push_back(sf::Vertex(sf::Vector2f(SIDEBAR_ORIGIN_X + SIDEBAR_LENGTH, 0), sf::Color(64, 64, 64)));

	//HP and MP BAR
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X, HP_ORIGIN_Y), sf::Color(255, 255, 255)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X, HP_ORIGIN_Y + HP_WIDTH), sf::Color(255, 255, 255)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_LENGTH, HP_ORIGIN_Y + HP_WIDTH), sf::Color(255, 255, 255)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_LENGTH, HP_ORIGIN_Y), sf::Color(255, 255, 255)));

	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BORDER, HP_ORIGIN_Y + HP_BORDER), sf::Color(128, 128, 128)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BORDER, HP_ORIGIN_Y + HP_WIDTH - HP_BORDER), sf::Color(64, 64, 64)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_LENGTH - HP_BORDER, HP_ORIGIN_Y + HP_WIDTH - HP_BORDER), sf::Color(80, 80, 80)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_LENGTH - HP_BORDER, HP_ORIGIN_Y + HP_BORDER), sf::Color(128, 128, 128)));

	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X, HP_ORIGIN_Y + HP_BAR_Y), sf::Color(204, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_BAR_Y), sf::Color(100, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X + HP_BAR_LENGTH, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_BAR_Y), sf::Color(100, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X + HP_BAR_LENGTH, HP_ORIGIN_Y + HP_BAR_Y), sf::Color(204, 0, 0)));

	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X, HP_ORIGIN_Y + HP_BAR_Y), sf::Color(0, 204, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_BAR_Y), sf::Color(0, 100, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X + HP_BAR_LENGTH*(hp / hpmax), HP_ORIGIN_Y + HP_BAR_WIDTH + HP_BAR_Y), sf::Color(0, 100, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_BAR_X + HP_BAR_LENGTH*(hp / hpmax), HP_ORIGIN_Y + HP_BAR_Y), sf::Color(0, 204, 0)));

	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X, HP_ORIGIN_Y + HP_MPBAR_Y), sf::Color(204, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_MPBAR_Y), sf::Color(100, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X + HP_BAR_LENGTH, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_MPBAR_Y), sf::Color(100, 0, 0)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X + HP_BAR_LENGTH, HP_ORIGIN_Y + HP_MPBAR_Y), sf::Color(204, 0, 0)));

	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X, HP_ORIGIN_Y + HP_MPBAR_Y), sf::Color(0, 0, 204)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X, HP_ORIGIN_Y + HP_BAR_WIDTH + HP_MPBAR_Y), sf::Color(0, 0, 100)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X + HP_BAR_LENGTH*(mp / mpmax), HP_ORIGIN_Y + HP_BAR_WIDTH + HP_MPBAR_Y), sf::Color(0, 0, 100)));
	vertices.push_back(sf::Vertex(sf::Vector2f(HP_ORIGIN_X + HP_MPBAR_X + HP_BAR_LENGTH*(mp / mpmax), HP_ORIGIN_Y + HP_MPBAR_Y), sf::Color(0, 0, 204)));

	window.draw(&vertices[0], vertices.size(), sf::Quads);
	inventory.drawToWindow(window);

	sf::Text hpText("HP: " + std::to_string((int) hp) + " / " + std::to_string((int) hpmax), hpFont, 20);
	hpText.setStyle(sf::Text::Bold);
	hpText.setColor(sf::Color::White);
	hpText.setPosition(sf::Vector2f(HP_ORIGIN_X + HP_TEXT_X, HP_ORIGIN_Y + HP_TEXT_Y));
	window.draw(hpText);
	hpText.setString("MP: " + std::to_string((int) mp) + " / " + std::to_string((int) mpmax));
	hpText.setPosition(sf::Vector2f(HP_ORIGIN_X + HP_MPTEXT_X, HP_ORIGIN_Y + HP_MPTEXT_Y));
	window.draw(hpText);

	time_t currentTime;
	struct tm localTime;

	time(&currentTime);                   // Get the current time
	localtime_s(&localTime, &currentTime);  // Convert the current time to the local time

	int hour = localTime.tm_hour;
	if (hour > 12)
		hour = hour % 12;
	int min = localTime.tm_min;
	int sec = localTime.tm_sec;
	std::string ampm = ((localTime.tm_hour >= 12) ? "PM" : "AM");

	sf::Text timeText(std::to_string(hour) + ":" + std::to_string(min) + ":" + std::to_string(sec) + " " + ampm, timeFont, 50);
	timeText.setColor(sf::Color::Black);
	timeText.setPosition(1640, 50);
	window.draw(timeText);
	window.draw(minimap);
}