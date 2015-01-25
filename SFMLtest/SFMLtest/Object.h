#ifndef OBJECT_H
#define OBJECT_H
#include "Point.h"
class Object :public sf::Sprite
{
protected:
	int originx;
	int originy;
	bool done = false;
	Point isopos;
	int id;
	bool drawn = false;
public:
	Object(int newid,const sf::Texture& tex, const sf::Vector2f& pos, bool centre)
	{
		id = newid;
		setTexture(tex);
		setPosition(pos);
		if (centre)
			setOrigin((float)(tex.getSize().x / 2.0), (float)(tex.getSize().y / 2.0));
		originx=tex.getSize().x / 2;
		originy=tex.getSize().y / 2;
	}
	Object(int id, const sf::Texture& tex, float x, float y, bool centre) :Object(id, tex, sf::Vector2f(x, y), centre)
	{
	}
	void setDone(bool d)
	{
		done = d;
	}
	bool getDone()
	{
		return done;
	}
	void setPos(Point position)
	{
		isopos = position;
	}
	Point getPos()
	{
		return isopos;
	}
	void setID(int newid)
	{
		id = newid;
	}
	void setDrawn(bool newDrawn)
	{
		drawn = newDrawn;
	}
	bool getDrawn()
	{
		return drawn;
	}
	int getID()
	{
		return id;
	}
};
#endif