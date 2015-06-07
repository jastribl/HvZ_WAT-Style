#include "stdafx.h"
#include "Constants.h"
#include "Hud.h"
#include <iostream>

Hud::Hud() {
	font.loadFromFile("Resources/Fonts/arial.ttf");
}

Hud::~Hud() {
}

void Hud::setHP(float newhp){
	hp = newhp;
}

void Hud::setMP(float newmp){
	mp = newmp;
}

void Hud::drawToWindow(sf::RenderWindow& window) {
	static int HP_BORDER = 3;
	static int HP_LENGTH = 300;
	static int HP_WIDTH = 150;
	static int HP_ORIGIN_X = SCREEN_SIZE_X - HP_LENGTH - (HP_BORDER * 2);
	static int HP_ORIGIN_Y = SCREEN_SIZE_Y - HP_WIDTH - (HP_BORDER * 2);
	static int HP_TEXT_X = 20;
	static int HP_TEXT_Y = 15;
	static int HP_MPTEXT_X = 20;
	static int HP_MPTEXT_Y = 70;
	static int HP_BAR_X = 20;
	static int HP_BAR_Y = 45;
	static int HP_BAR_LENGTH = 260;
	static int HP_BAR_WIDTH = 20;
	static int HP_MPBAR_X = 20;
	static int HP_MPBAR_Y = 100;

	std::vector<sf::Vertex> vertices;
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

	sf::Text text("HP: " + std::to_string((int)hp) + " / " + std::to_string((int)hpmax), font, 20);
	text.setStyle(sf::Text::Bold);
	text.setColor(sf::Color::White);
	text.setPosition(sf::Vector2f(HP_ORIGIN_X + HP_TEXT_X, HP_ORIGIN_Y + HP_TEXT_Y));
	window.draw(text);
	text.setString("MP: " + std::to_string((int)mp) + " / " + std::to_string((int)mpmax));
	text.setPosition(sf::Vector2f(HP_ORIGIN_X + HP_MPTEXT_X, HP_ORIGIN_Y + HP_MPTEXT_Y));
	window.draw(text);
}