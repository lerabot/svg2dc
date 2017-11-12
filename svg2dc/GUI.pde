int itemList;
String itemName;
String mapName = "No Map";
String[] mapInfo;
ArrayList<File_> files;

void setGUI() {
  mapInfo = new String[10];
  mapInfo[0] = "No Map"; // map name
  mapInfo[1] = "0"; //object number
  mapInfo[2] = "No Error"; //errors
  mapInfo[3] = "0"; //memory used
  mapInfo[4] = "0"; //texture loaded
  mapInfo[5] = "0"; //texture loaded
  List f = Arrays.asList(filenames);
  gui.addButton("Select File").setPosition(110, 27).setSize(60, 20);
  //gui.addScrollableList("files").setPosition(110, 10).addItems(f);
  gui.addButton("Output Folder").setPosition(500, 7).setSize(90, 20);
}

void drawGUI() {
  int line = 1;
  int size = 20;

  text("Default Folder : " + destFolder, 20, line * size);
  line++;

  //map name
  text("Choose SVG > ", 20, line * size);
  text(mapInfo[0], 180, line * size); 
  line++;

  //item data
  text(mapInfo[1] + " objects & " + mapInfo[4] + " textures", 20, line * size);
  line++;

  float memLeft = int(float(mapInfo[3]) / 3500000 * 100);
  text("Memory used : " + mapInfo[3] + "ko / " + memLeft + "%", 20, line * size);
  line++;

  text(mapInfo[2], 20, line * size);
  line++;
}

void controlEvent(ControlEvent theEvent) {
  /* events triggered by controllers are automatically forwarded to 
   the controlEvent method. by checking the name of a controller one can 
   distinguish which of the controllers has been changed.
   */

  /* check if the event is from a controller otherwise you'll get an error
   when clicking other interface elements like Radiobutton that don't support
   the controller() methods
   */

  if (theEvent.isController()) { 

    //print("control event from : "+theEvent.getController().getName());
    //println(", value : "+theEvent.getController().getValue());

    if (theEvent.getController().getName()=="Select File") {
      selectInput("Select a .svg file:", "getSVG");
    }
    if (theEvent.getController().getName()=="Output Folder") {
      File dir = new File("/home/lerabot/Dreamcast/kos/game_build/pheonix/");
      selectFolder("Select a folder to process:", "folderSelected", dir);
    }
  }
}

void getSVG(File selection) {
  if (selection != null) {
    if (selection.toString().endsWith("svg")) {
      filePath = selection.getParent() + "/";
      loadMap(selection.toString());
    } else
      println("Not a .svg file");
  }
}

void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    destFolder = selection.toString();
    println("User selected " + selection.getAbsolutePath());
  }
}