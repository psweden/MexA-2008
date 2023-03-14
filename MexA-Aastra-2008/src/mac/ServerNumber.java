package mac;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Choice;
import java.io.IOException;


// A canvas that illustrates drawing on an Image
public class ServerNumber extends Canvas {

    private DTMFRequestSony mps;
    private String switchBordNumber, emei, accessNumber, viewDateString;
    private String regYES = "Ja", regNO = "Nej", setString;

    public ServerNumber(String sN, /*, String IMEI, String star,*/
                        String access, String ViewDateString) { // Tar emot värden från huvudclassen i konstruktorn.

        this.switchBordNumber = sN;
        this.emei = "";
        this.accessNumber = access;
        this.viewDateString = ViewDateString;

        /*if(star.equals("*")){
         this.setString = regYES;
            }
            if(star.equals("1")){

         this.setString = regNO;

            }*/

    }

    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        // Create an Image the same size as the
        // Canvas.
        Image image = Image.createImage(width, height);
        Graphics imageGraphics = image.getGraphics();

        // Fill the background of the image black
        imageGraphics.setColor(0x000000);
        imageGraphics.fillRect(0, 0, width, height);

        // Draw a pattern of lines
        int count = 10;
        int yIncrement = height / count;
        int xIncrement = width / count;

        // Add some text
        imageGraphics.setFont(Font.getFont(Font.FACE_PROPORTIONAL, 0,
                                           Font.SIZE_SMALL));
        imageGraphics.setColor(0xffff00);
        imageGraphics.drawString("mobisma", width / 2, 10,
                                 Graphics.TOP | Graphics.HCENTER);
        imageGraphics.drawString("Mobile extension Ver 1.0", width / 2, 25,
                                 Graphics.TOP | Graphics.HCENTER);

        imageGraphics.setColor(0xffffff);
        try {
            Image image1 = Image.createImage("/icon/boxthumb1.png");
            imageGraphics.drawImage(image1, width / 2, 45,
                                    Graphics.TOP | Graphics.HCENTER);
        } catch (IOException ex) {
        }

        imageGraphics.drawString("Registrered: ", width / 2, 140,
                                 Graphics.TOP | Graphics.HCENTER);
        imageGraphics.drawString("to. " + viewDateString, width / 2, 155,
                                 Graphics.TOP | Graphics.HCENTER);
        //imageGraphics.drawString(emei, width/2, 75, Graphics.TOP | Graphics.HCENTER);

        //imageGraphics.drawString("www.mobisma.com", width/2, 135, Graphics.TOP | Graphics.HCENTER);
        //imageGraphics.drawString("support@mobisma.com", width/2, 150, Graphics.TOP | Graphics.HCENTER);

        // Copy the Image to the screen
        g.drawImage(image, 0, 0, Graphics.TOP | Graphics.LEFT);

    }


}
