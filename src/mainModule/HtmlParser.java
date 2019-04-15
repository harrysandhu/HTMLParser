package mainModule;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlParser {
    public static String getId(String line) {
        int start = line.indexOf("\"") + 1;
        int end = line.indexOf("\"", start);
        return line.substring(start,  end);
    }

    public static String getName(String line){
        String name = "";
        int end = 0;
        end = line.indexOf(' ');
        name = line.substring(0, end);
        return name;
    }

    public static String getCss(String id, String cssTextBuffer){
        int start = cssTextBuffer.indexOf(id + "{");
        if(start != -1) {
            start = start + id.length() + 1;
            int end = cssTextBuffer.indexOf("}", start);
            return cssTextBuffer.substring(start, end);
        }
        else
            return "";
   }

  public static void main(String[] args) throws FileNotFoundException {
      if(args[0] == null){
          System.out.println("Enter an absolute path of html file.");
          System.exit(0);
      }
      String htmlFileName = args[0];

      File file = new File(htmlFileName);
      Scanner sc = new Scanner(file);
      boolean bodyStarted = false;
      //buffer array to keep track of active tag
      ArrayList<Tag> nodesBuffer = new ArrayList<Tag>();
      //html root element
      Tag htmlRootTag = new Tag("html");
      nodesBuffer.add(htmlRootTag);

      String id = "";
      String name = "";

      HashMap<Tag, ArrayList<Tag>> Nodes = new HashMap<>();
      //tags list to assign all children to parent active node.
        String cssTextBuffer = "";
        boolean cssStarted = false;
        boolean tagStarted = false;
      while (sc.hasNextLine()){
          String line = sc.nextLine().trim().toLowerCase();
          if(bodyStarted == false && line.contains("<style>")){
              cssStarted = true;
          }
          else if(bodyStarted == false && line.contains("</style")){
              cssStarted = false;
          }
          else if(cssStarted){
              cssTextBuffer = cssTextBuffer.concat(line);
          }
          else if(bodyStarted == false && line.contains("body")){
              bodyStarted = true;
          }
          if(bodyStarted && line.length() >= 1){
              //check for start of a normal tag
              //insert it into hashtable
              //if tag is starting
            if(line.charAt(0) == '<' && line.charAt(1) != '/'){
                line = line.substring(1, line.length() -1);
                tagStarted = true;
                //get tag name and id from line
                name = getName(line);
                id = getId(line);
                //new tag

                Tag newChildTag = new Tag(name, id);
                String cssText = getCss(newChildTag.getTagId(), cssTextBuffer);

                newChildTag.setCss(cssText);
                System.out.println(id + ":-> "+ newChildTag.getCssText());
                System.out.println(name + "::" + id);
                //get the active tag
                Tag parentTag = nodesBuffer.get(nodesBuffer.size() -1);
                //get active tag's children

                ArrayList<Tag> childTags = new ArrayList<>();
                if(Nodes.get(parentTag) == null){
                    Nodes.put(parentTag, childTags);
                }else {
                    //make a copy to child tags
                    for (Tag t : Nodes.get(parentTag)) {
                        childTags.add(t);
                    }
                }
                //and add the new child tags to parent tag's children
                childTags.add(newChildTag);
                Nodes.put(parentTag, childTags);
                nodesBuffer.add(newChildTag);
            }

            //check for end of tag
            //will always return true for nodesBuffer's last element
            else if(line.charAt(0) == '<' && line.charAt(1) == '/' ){//&& line.contains(nodesBuffer.get(nodesBuffer.size() - 1).getTagName())){
                System.out.println(line + ": got closed.");
                nodesBuffer.remove(nodesBuffer.size() - 1);
                tagStarted = false;
            }
            else if(tagStarted){
                Tag activeTag = nodesBuffer.get(nodesBuffer.size() -1);
                activeTag.setText(line);
            }
          }
      }
     ArrayList<String> jsCodeLines = new ArrayList<>();

    Nodes.forEach((k, v) -> {
        for(Tag t : v){
            String createElement = "var "+ t.getTagName()+ t.getTagId() + " = " + "document.createElement('"+ t.getTagName()+"');\n";
            jsCodeLines.add(createElement);
        }
    });
      Nodes.forEach((k, v) -> {
          for(Tag t : v){
              if(t.getText() != ""){
                  String textNode = "var " + t.getTagId() + "text =  document.createTextNode(\""+ t.getText()+ "\");\n";
                  jsCodeLines.add(textNode);
                  String appendTextNode = t.getTagName()+ t.getTagId() + ".appendChild("+  t.getTagId() + "text);\n";
                  jsCodeLines.add(appendTextNode);
              }
          }
      });

      Nodes.forEach((k, v) -> {
          for(Tag t : v){
              if(t.getCssText() != "") {
                  String elementStyle = t.getTagName() + t.getTagId() + ".style.cssText = \"" + t.getCssText() + "\";\n";
                  jsCodeLines.add(elementStyle);
              }
          }
      });

        Nodes.forEach((k, v) -> {
            for(Tag t : v){
                String appendChild = k.getTagName()+ k.getTagId() + ".appendChild("+ t.getTagName()+ t.getTagId() +");\n";
                jsCodeLines.add(appendChild);
            }


    });
      try {
          FileWriter fileWriter = new FileWriter("<filepath.......>");
          fileWriter.write("<!DOCTYPE html>\n" +
                  "<html>\n" +
                  "<head>\n" +
                  "\t<title></title>\n" +
                  "</head>\n" +
                  "<body>\n" +
                  "<script type=\"text/javascript\">");
          for(String s : jsCodeLines) {
                if(s.contains("var bodymybody =" )){
                    s = "\nvar bodymybody = document.getElementsByTagName('body')[0]\n";
                }else if(s.contains("htmlnull.appendChild(bodymybody)")){
                    s = "";
                }
              fileWriter.write(s);
          }
          fileWriter.write("</script>\n" +
                  "</body>\n" +
                  "</html>");
          fileWriter.close();
        System.out.println("Successfully written "+ jsCodeLines.size() +" JavaScript lines!");
      }catch(IOException ex){
          System.out.println(ex);
      }
  }


}

