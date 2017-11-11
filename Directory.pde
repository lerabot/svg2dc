import static java.nio.file.StandardCopyOption.*;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

int copyTexture() {
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


String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

String[] listSVG(String[] fileNames) {
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
File[] listFiles(String dir) {
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
ArrayList<File> listFilesRecursive(String dir) {
  ArrayList<File> fileList = new ArrayList<File>(); 
  recurseDirSVG(fileList, dir);
  return fileList;
}

// Recursive function to traverse subdirectories
void recurseDir(ArrayList<File> a, String dir) {
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
void recurseDirSVG(ArrayList<File> a, String dir) {
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