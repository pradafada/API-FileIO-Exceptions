import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.net.*;

class Main {
  
  public static ArrayList<String> getData () {
    ArrayList<String> data = new ArrayList<String>();
    try {
      String dataline = "";
      Socket socket = new Socket("api.coindesk.com",80);
      OutputStream os = socket.getOutputStream();
      PrintWriter pw = new PrintWriter(os,true);
      pw.println("GET http://api.coindesk.com/v1/bpi/currentprice.json                         HTTP/1.0\n\n");
      pw.flush();
      Scanner scan = new Scanner(socket.getInputStream());

      while (scan.hasNextLine()) {
        data.add(scan.nextLine());
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return data;
  }
  
  public static float getDollarPrice(ArrayList<String>lines) {
    boolean header=true;
    String json="";
    for(String line : lines) {
      if(line.equals("")) {
        header=false;
        continue;
      }
      if(header==false) {
        json=line;
        break;
      }
    }
    //System.out.println("Json: "+json);
    String[] jsonParts=json.split(":");
    String priceLine=jsonParts[19];
    String justPrice=priceLine.replace("},\"GBP\"","");
    float price=Float.parseFloat(justPrice);
    return price;
  }

  public static void buyBitCoin (float bitPrice) {
    try {
      File myFile = new File("initialInvestmentUSD.txt");
      Scanner scan = new Scanner(myFile);
      String line = "";

      File myFile2 = new File("clientBC.txt");
      PrintWriter pw = new PrintWriter(myFile2);

      while (scan.hasNextLine()) {
        line = scan.nextLine();

        String[] newLine = line.split(":");
        float bitNum = Float.parseFloat(newLine[1])/bitPrice;
        pw.println(newLine[0]+ ":" +bitNum);
      }
      pw.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static void getCurrentValue (float bitCurrent) {
    try {
      File myFile = new File("clientBC.txt");
      Scanner scan = new Scanner(myFile);
      String line = "";

      while (scan.hasNextLine()) {
        line = scan.nextLine();

        String[] newLine = line.split(":");
        float bitNum = Float.parseFloat(newLine[1])*bitCurrent;
        System.out.println(newLine[0]+ ":" +bitNum);
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static float getPersonFromFile (String person, String search) throws PersonNotFound{
    try {
      File myFile = new File(search);
      Scanner scan = new Scanner(myFile);
      String line = "";

      while (scan.hasNextLine()) {
        line = scan.nextLine();
        String[] newLine = line.split(":"); 
  
        if (person.equals(newLine[0])) {
          return Float.parseFloat(newLine[1]);
        }
      }
      throw new PersonNotFound ("Person not found.");
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return 0;
  }

  
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    int choice;
    String name;
    do {
      ArrayList<String> bitcoin = getData();
      float price = getDollarPrice(bitcoin);

      System.out.println("One BitCoin is currently worth $" +price+ "\n1. Buy Bitcoin\n2. See everyones current value in USD\n3. See one persons gain/loss\n4. Quit");
      choice = scan.nextInt();
      scan.nextLine();

      switch(choice) {
        case 1:
          buyBitCoin(price);
          break;
        case 2:
          getCurrentValue(price);
          break;
        case 3:
          System.out.println("Enter a name");
          name = scan.nextLine();
            
          try {
            float BC = getPersonFromFile(name, "clientBC.txt");
            float og = getPersonFromFile(name, "initialInvestmentUSD.txt");
            float cv = BC*price;
          
            System.out.println("Original Investment: $"           
            +og+ "\nNumber of bitcoins: " 
            +BC+ "\nCurrent Value: $" 
            +cv+ "\nChange in value: $" 
            +(cv-og));
          }
          catch (Exception e) {
            System.out.println(e.getMessage());
          }
          break;  
      }
      
    } while(choice != 4);
    
  }
}