#include "stdafx.h"
#include "Block.h"
#include "Point.h"

Block::Block() {}

Block::Block(int blockType, Point blockLocation) {
	type = blockType;
	location = blockLocation;
}

Block::~Block() {}

int Block::getType(){
	return type;
}

void Block::setType(int blockType){
	type = blockType;
}

Point Block::getLocation() const{
	return location;
}

int Block::getX() const{
	return location.getX();
}
int Block::getY() const{
	return location.getY();
}

void Block::setLocation(Point blockLocation){
	location = blockLocation;
}