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

void loadMap(String fileName) {
  raw = loadXML(fileName);
  //mapTitle = raw.getChild("g").getString("title");
  scene = createWriter(destFolder + "/mapdata.c");
  files = new ArrayList<File_>();
  mapInfo[0] = "Map Loaded";
  generateMap();
}

void generateMap() { 
  scene.println("void load" + mapTitle + "(scene* self) {");
  printMapSize(raw);
  printImageData(raw);
  //scene.println("self->updateScene = update" + mapTitle + ";");
  scene.print("}");
  scene.flush();
  scene.close();
  copyTexture();
}


void printMapSize(XML raw) {
  mapSize = new PVector(raw.getInt("width"), raw.getInt("height"));
  scene.println("////MAP DATA////");
  scene.println("self->mapSize[0] = " + int(mapSize.x) + ";");
  scene.println("self->mapSize[1] = " + int(mapSize.y) + ";");
  scene.println();
}

void printImageData(XML raw) {
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

      if (int(size.x) % 16 != 0 || int(size.y) % 16 != 0)
        mapInfo[2] += "Invalid size image " + j + " / filename = " + fileName + "\n";
      

      pos.x = pos.x + size.x/2;
      pos.y = mapSize.y - size.y/2 - pos.y;

      scene.println("//"+ i.getString("id"));
      if (isLoaded(fileName, j) > -1) {
        scene.print("self->obj[" + j + "] = createObject(");
        scene.println( "\"\", " + int(pos.x) + ", " + int(pos.y) + ", 1);");
        scene.println("self->obj[" + j + "].t = self->obj[" + isLoaded(fileName, j) + "].t;");
      } else {
        scene.print("self->obj[" + j + "] = createObject(");
        scene.println( "\"/rd/" + fileName + "\", " + int(pos.x) + ", " + int(pos.y) + ", 1);");
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

int isLoaded(String fileName, int num) {
  for (int i = 0; i < files.size(); i++) {
    String f = files.get(i).name;
    if (fileName.equals(f))
      return(files.get(i).num);
  }
  files.add(new File_(fileName, num));
  return(-1);
}

int getImgNum(XML[] img) {
  int j = 0; 
  for (XML i : img) {
    if (i.getName().equals("image"))
      j++;
  }
  return(j);
}

int defineName(XML[] img) {
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