import controlP5.*;

import java.util.Date;
import java.util.*;

String[] filenames;

ControlP5 gui;

void setup() {
  size(600, 200);
  String path = sketchPath();
  filenames = listFileNames(path);
  filenames = listSVG(filenames);
  gui = new ControlP5(this);
  setGUI();
}

void draw() {
  background(0);
  drawGUI();
  //text(gui.getController(ScrollableList.,), 20, 50);
}