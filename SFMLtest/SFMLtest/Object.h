#ifndef OBJECT_H
#define OBJECT_H
class Object :public sf::Sprite
{
protected:
	int originx;
	int originy;
	bool done = false;
public:
	Object(const sf::Texture& tex, const sf::Vector2f& pos, bool centre)
	{
		setTexture(tex);
		setPosition(pos);
		if (centre)
			setOrigin((float)(tex.getSize().x / 2.0), (float)(tex.getSize().y / 2.0));
		originx=tex.getSize().x / 2;
		originy=tex.getSize().y / 2;
	}
	Object(const sf::Texture& tex, float x, float y, bool centre) :Object(tex, sf::Vector2f(x, y), centre)
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
};
#endif