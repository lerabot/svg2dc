import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import java.util.Date; 
import java.util.*; 
import static java.nio.file.StandardCopyOption.*; 
import java.io.IOException; 
import java.nio.file.CopyOption; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.nio.file.StandardCopyOption; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class svg2dc extends PApplet {






String[] filenames;

ControlP5 gui;

public void setup() {
  
  String path = sketchPath();
  filenames = listFileNames(path);
  filenames = listSVG(filenames);
  gui = new ControlP5(this);
  setGUI();
}

public void draw() {
  background(0);
  drawGUI();
  //text(gui.getController(ScrollableList.,), 20, 50);
}








public int copyTexture() {
  CopyOption[] options = new CopyOption[] 
    {
    StandardCopyOption.REPLACE_EXISTING, 
    StandardCopyOption.COPY_ATTRIBUTES
  };

  for (File_ f : files) {
    Path source = Paths.get(filePath + f.name); //PAS BON
    Path dest = Paths.get(destFolder + "/" + f.name);
    try
    {
      Files.copy(source, dest, options);
      println(f.name + " copied.");
    } 
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
  return 0;
}


public String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

public String[] listSVG(String[] fileNames) {
  StringList fileSVG;
  fileSVG = new StringList();
  for (String f : fileNames) {
    if (f.endsWith("svg"))
      fileSVG.append(f);
  }

  return fileSVG.array();
}

// This function returns all the files in a directory as an array of File objects
// This is useful if you want more info about the file
public File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}

// Function to get a list of all files in a directory and all subdirectories
public ArrayList<File> listFilesRecursive(String dir) {
  ArrayList<File> fileList = new ArrayList<File>(); 
  recurseDirSVG(fileList, dir);
  return fileList;
}

// Recursive function to traverse subdirectories
public void recurseDir(ArrayList<File> a, String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    // If you want to include directories in the list
    a.add(file);  
    File[] subfiles = file.listFiles();
    for (int i = 0; i < subfiles.length; i++) {
      // Call this function on all files in this directory
      recurseDir(a, subfiles[i].getAbsolutePath());
    }
  } else {
    a.add(file);
  }
}

// Recursive function to traverse subdirectories
public void recurseDirSVG(ArrayList<File> a, String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    // If you want to include directories in the list
    a.add(file);  
    File[] subfiles = file.listFiles();
    for (int i = 0; i < subfiles.length; i++) {
      // Call this function on all files in this directory
      recurseDir(a, subfiles[i].getAbsolutePath());
    }
  } else {
    a.add(file);
  }
}
XML raw;
PrintWriter scene;
PVector mapSize;
String mapTitle;
String destFolder = "/home/lerabot/Dreamcast/kos/game_build/pheonix/romdisk/";
String filePath; //the selected file path

class File_ {
  String name;
  int num;


  public File_(String name, int num) {
    this.name = name;
    this.num = num;
  }
}

public void loadMap(String fileName) {
  raw = loadXML(fileName);
  //mapTitle = raw.getChild("g").getString("title");
  scene = createWriter(destFolder + "/mapdata.c");
  files = new ArrayList<File_>();
  mapInfo[0] = "Map Loaded";
  generateMap();
}

public void generateMap() { 
  scene.println("void load" + mapTitle + "(scene* self) {");
  printMapSize(raw);
  printImageData(raw);
  //scene.println("self->updateScene = update" + mapTitle + ";");
  scene.print("}");
  scene.flush();
  scene.close();
  copyTexture();
}


public void printMapSize(XML raw) {
  mapSize = new PVector(raw.getInt("width"), raw.getInt("height"));
  scene.println("////MAP DATA////");
  scene.println("self->mapSize[0] = " + PApplet.parseInt(mapSize.x) + ";");
  scene.println("self->mapSize[1] = " + PApplet.parseInt(mapSize.y) + ";");
  scene.println();
}

public void printImageData(XML raw) {
  XML images[];
  int j = 0;
  PVector t;
  PVector pos;
  PVector size;
  String fileName, transform;
  int memorySize = 0;

  images = raw.getChild("g").getChildren();
  scene.println("////OBJECT DATA////");
  //defineName(images);
  scene.println("self->objNum = " + getImgNum(images) + ";");
  scene.println("self->obj = malloc(self->objNum * sizeof(gameObject));");
  mapInfo[1] = str(getImgNum(images));
  mapInfo[2] = "";

  for (XML i : images) {
    if (i.getName().equals("image")) {
      size = new PVector(round(i.getFloat("width")), round(i.getFloat("height")));
      pos = new PVector(round(i.getFloat("x")), round(i.getFloat("y")));
      fileName = i.getString("xlink:href");

      if (pos.x < 0)
        pos.x = (pos.x * -1) - size.x;
      if (pos.y < 0)
        pos.y = (pos.y * -1) - size.y;  

      if (PApplet.parseInt(size.x) % 16 != 0 || PApplet.parseInt(size.y) % 16 != 0)
        mapInfo[2] += "Invalid size image " + j + " / filename = " + fileName + "\n";
      

      pos.x = pos.x + size.x/2;
      pos.y = mapSize.y - size.y/2 - pos.y;

      scene.println("//"+ i.getString("id"));
      if (isLoaded(fileName, j) > -1) {
        scene.print("self->obj[" + j + "] = createObject(");
        scene.println( "\"\", " + PApplet.parseInt(pos.x) + ", " + PApplet.parseInt(pos.y) + ", 1);");
        scene.println("self->obj[" + j + "].t = self->obj[" + isLoaded(fileName, j) + "].t;");
      } else {
        scene.print("self->obj[" + j + "] = createObject(");
        scene.println( "\"/rd/" + fileName + "\", " + PApplet.parseInt(pos.x) + ", " + PApplet.parseInt(pos.y) + ", 1);");
        memorySize += size.x * size.y * 2;
      }
      //UV
      if ( i.getFloat("u") != 0 || i.getFloat("v") != 0)
        scene.println("               setUV(&self->obj[" + j +"].t, " + i.getFloat("u") + ", " + i.getFloat("v") + ");");

      //anim
      if ( i.getFloat("anim") != 0)
        scene.println("               setAnim(&self->obj[" + j +"].t, " + i.getInt("anim") + ");");

      //flipU
      if ( i.getFloat("flipU") != 0 || i.getFloat("x") < 0)
        scene.println("               flipU(&self->obj[" + j +"].t);");

      j++;
    }
  }
  mapInfo[4] = str(files.size());
  mapInfo[3] = str(memorySize);
  scene.println();
}

public int isLoaded(String fileName, int num) {
  for (int i = 0; i < files.size(); i++) {
    String f = files.get(i).name;
    if (fileName.equals(f))
      return(files.get(i).num);
  }
  files.add(new File_(fileName, num));
  return(-1);
}

public int getImgNum(XML[] img) {
  int j = 0; 
  for (XML i : img) {
    if (i.getName().equals("image"))
      j++;
  }
  return(j);
}

public int defineName(XML[] img) {
  int j = 0; 
  for (XML i : img) {
    if (i.getName().equals("image")) {
      scene.println("#define " + i.getString("id") + " " + j);
      j++;
    }
  }
  scene.println();
  return(j);
}
int itemList;
String itemName;
String mapName = "No Map";
String[] mapInfo;
ArrayList<File_> files;

public void setGUI() {
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

public void drawGUI() {
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

  float memLeft = PApplet.parseInt(PApplet.parseFloat(mapInfo[3]) / 3500000 * 100);
  text("Memory used : " + mapInfo[3] + "ko / " + memLeft + "%", 20, line * size);
  line++;

  text(mapInfo[2], 20, line * size);
  line++;
}

public void controlEvent(ControlEvent theEvent) {
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

public void getSVG(File selection) {
  if (selection != null) {
    if (selection.toString().endsWith("svg")) {
      filePath = selection.getParent() + "/";
      loadMap(selection.toString());
    } else
      println("Not a .svg file");
  }
}

public void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    destFolder = selection.toString();
    println("User selected " + selection.getAbsolutePath());
  }
}
  public void settings() {  size(600, 200); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "svg2dc" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
