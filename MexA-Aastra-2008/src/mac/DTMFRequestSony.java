package mac;


import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreNotOpenException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import javax.microedition.rms.RecordEnumeration;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;
import java.io.EOFException;
import java.io.InputStreamReader;
import com.sun.j2me.global.LocaleHelpers;


public class DTMFRequestSony extends MIDlet implements CommandListener,
        ItemStateListener,
        Runnable {

    private RecordStore addrBook;

    private static final int FN_LEN = 10;

    private static final int LN_LEN = 20;

    private static final int PN_LEN = 15;

    final private static int ERROR = 0;

    final private static int INFO = 1;

    private Display display;

    private Alert alert;

    private Command cmdAdd;

    private Command cmdBack;

    private Command cmdCancel;

    private Command cmdDial;

    private Command cmdExit;

    private Command cmdSelect;

    private Command cmdSearchNetwork;

    private Command cmdSearchLocal;

    public List mainScr;

    private String[] mainScrChoices = {"Sök Kontakt", "Lägg till Kontakt",
                                      "Visa Kontakter", "Sortera Kontakter"};

    private Form searchScr;

    private TextField s_lastName;

    private TextField s_firstName;

    private Form entryScr;

    private TextField e_lastName;

    private TextField e_firstName;

    private TextField e_phoneNum;

    private List nameScr;

    private Vector phoneNums;

    private Form optionScr;

    private ChoiceGroup sortChoice;

    private TextBox dialScr;

    private int sortOrder = 1;


    private Ticker mainTicker, callListTicker;
    private TextBox dialTextBox, connectTextBox;
    private Form editSettingForm, editWebSiteForm;
    public List callList, nationell_List, mainList;

    private Command DialCommand, DialCallListCommand, ConnectCommand,
    ConnectDialCommand, ConnectDialBackCommand,
    AboutCommand, minimazeCommand, helpCommand, editSettingBackCommand,
    editSettingCancelCommand, editSettingSaveCommand, propertiesCommand,
    backCommand, settingsCommand, settingsListCancelCommand,
    callListDialCommand, callListBackCommand, ExitCommandMainList,
    meny, goBackCommand, editWebSiteBackCommand, editWebSiteCancelCommand,
    editWebsiteSaveCommand, urlCommand, emptyCallListCommand;


    private Command thCmd;
    private StringItem item;
    private String stringTotal, validateNumber;
    private int type = 0;
    private String SOS; // Nokia >> ('p' hela serie 40) ('/' , '/p' serie 60)
    private String setP = ";postd="; // Sonyericsson >> (';postd=' för mobiltelefoner)  ('/p' för pda-modeller)
    private String accessCode, internNumber;
    private String identy, checkIdenty;
    private String sortString;
    private String[] subStr;
    private String accessNumber, switchBoardNumber, extensionNumber,
    pinCodeNumber, setMounth, setDate, setYear,
    DBdate, DBmounth, DByear, DBdateBack, DBmounthBack, DByearBack, getTWO,
    dateString, setViewMounth, ViewDateString, setdayBack,
    setmounthBack, setyearBack;
    private Alert alertEditSettings;

    public RecordStore recStore = null;
    static final String REC_STORE = "Data_Store_attendant_145";


    private TextField dateNumber, accessNumbers, editSwitchBoardNumber,
    editExtensionNumber, editPinCodeNumber, editWebSite;

    private int antalDagar;
    private int dayBack;
    private int mounthBack;
    private int yearBack;
    private int dayAfter;
    private int monthAfter;
    private int yearAfter;
    private int day;
    private int month;
    private int checkYear;

    private Date today;
    private String todayDate;
    private TimeZone tz = TimeZone.getTimeZone("GMT+1");

    // Tillhör sensast slagna nummer.
    private String mainCallString;

    // Nationell eller Internationell växeltyp.

    private String nationell, internationell, switchboardType; // kontrollerar om växeln är av typen >> internationell eller nationell.
    private String URL, domain;

    public DTMFRequestSony() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException, IOException {


        cmdAdd = new Command("Add", Command.OK, 1);
        cmdBack = new Command("H-meny", Command.BACK, 2);
        cmdCancel = new Command("Cancel", Command.BACK, 2);
        cmdDial = new Command("Dial", Command.OK, 1);
        cmdExit = new Command("Exit", Command.EXIT, 2);
        cmdSelect = new Command("Select", Command.OK, 1);
        cmdSearchNetwork = new Command("Network", Command.SCREEN, 4);
        cmdSearchLocal = new Command("Local", Command.SCREEN, 3);
        emptyCallListCommand = new Command("Radera alla", Command.OK, 1);

        alert = new Alert("", "", null, AlertType.INFO);
        alert.setTimeout(2000);

        try {
            addrBook = RecordStore.openRecordStore("TheAddressBook", true);
        } catch (RecordStoreException e) {
            addrBook = null;
        }

        this.tz = tz;

        today = new Date();
        today.getTime();
        today.toString();
        this.todayDate = today.toString();
        System.out.println(today);

        this.antalDagar = 30; // anger hur många dagar programmet ska vara öppet innan det stängs....

        try {
            this.domain = getURL();
        } catch (RecordStoreNotOpenException ex4) {
        } catch (InvalidRecordIDException ex4) {
        } catch (RecordStoreException ex4) {
        }
        try {
            this.accessNumber = getAccessNumber();
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            this.switchBoardNumber = getSwitchBoardNumber();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        try {
            this.extensionNumber = getExtensionNumber();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        try {
            this.pinCodeNumber = getPinCodeNumber();
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        this.accessCode = accessCode;
        this.internNumber = internNumber;
        this.accessNumber = accessNumber;

        this.setP = setP;
        this.SOS = "112";
        this.nationell = "";
        this.internationell = "2";
        this.identy = ""; // System.getProperty("com.sonyericsson.imei");
        this.checkIdenty = checkIdenty;

        this.day = day;
        this.month = month;
        setDBDate(); // OBS.. Det här metodanropet ska ligga här efter month och day.
        setDBDateBack();

        this.nationell = nationell;
        this.internationell = internationell;
        this.domain = domain;
        this.URL = URL;

        try {
            this.switchboardType = getSwitchBoardType();
        } catch (RecordStoreNotOpenException ex3) {
        } catch (InvalidRecordIDException ex3) {
        } catch (RecordStoreException ex3) {
        }

        //------------- MainList -----------------------------------------------

        mainList = new List("", Choice.IMPLICIT); // skapar en lista
        mainList.setTitle(null);
        mainTicker = new Ticker("Mobisma Mobile extension");
        mainList.setTicker(mainTicker);

        ExitCommandMainList = new Command("Avsluta", Command.EXIT, 2);
        settingsCommand = new Command("Inställningar", Command.SCREEN, 1);

        try {
            Image image1a = Image.createImage("/icon/systemphone24.png");
            Image image2a = Image.createImage("/icon/ring24.png");
            Image image5a = Image.createImage("/icon/samtalslista24.png");
            Image image10a = Image.createImage("/icon/minimera24.png");

            mainList.append("Koppla samtal", image1a);
            mainList.append("Ringa nummer", image2a);
            mainList.append("Samtalslista", image5a);
            mainList.append("Minimera", image10a);

            mainList.addCommand(ExitCommandMainList);
            mainList.addCommand(settingsCommand);
            mainList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        //------------- CallList ----------------------------------------------

        // tillhör 10 senast slagna nummer.
        callList = new List("Slagna Nummer", Choice.EXCLUSIVE);

        callList.append("Number 1", null);
        callList.append("Number 2", null);
        callList.append("Number 3", null);
        callList.append("Number 4", null);
        callList.append("Number 5", null);
        /*callList.append("Number 6", null);
                 callList.append("Number 7", null);
                 callList.append("Number 8", null);
                 callList.append("Number 9", null);
                 callList.append("Number 10", null);*/

        callListDialCommand = new Command("Dial", Command.OK, 1);
        callListBackCommand = new Command("Bakåt", Command.BACK, 2);

        callList.addCommand(callListDialCommand);
        callList.addCommand(callListBackCommand);
        callList.setCommandListener(this);

        //---- nationell-list ----------------------------


        //--------- SettingsList för växeltyp nationell eller internationell.

        nationell_List = new List("Växeltyp", Choice.IMPLICIT);
        nationell_List.append("Nationell", null);
        nationell_List.append("Internationell", null);

        settingsListCancelCommand = new Command("Avbryt", Command.CANCEL, 2);

        nationell_List.addCommand(settingsListCancelCommand);
        nationell_List.setCommandListener(this);

        //---------------- EDITSETTINGFORM -----------------------------------

        editSettingForm = new Form("Egenskaper");

        dateNumber = new TextField("dagensdatum: ", "", 32, TextField.ANY);
        accessNumbers = new TextField("Accessnummer: ", "", 32,
                                      TextField.NUMERIC);

        editSwitchBoardNumber = new TextField("Växelnummer: ", "", 32,
                                              TextField.PHONENUMBER);

        editExtensionNumber = new TextField("Anknytning: ", "", 32,
                                            TextField.PHONENUMBER);

        editPinCodeNumber = new TextField("Pin-Kod: ", "", 32,
                                          TextField.NUMERIC);

        editSettingBackCommand = new Command("Bakåt", Command.BACK, 1);
        editSettingCancelCommand = new Command("Avbryt", Command.BACK, 1);
        editSettingSaveCommand = new Command("Spara", Command.OK, 2);

        editSettingForm.addCommand(editSettingBackCommand);
        editSettingForm.addCommand(editSettingCancelCommand);
        editSettingForm.addCommand(editSettingSaveCommand);
        editSettingForm.setCommandListener(this);

        //------------------- web-site-address ---------------------------------

        editWebSiteForm = new Form("Egenskaper");

        editWebSite = new TextField("Webbaddress: ", "", 52, TextField.URL);

        editWebSiteBackCommand = new Command("Bakåt", Command.BACK, 1);
        editWebSiteCancelCommand = new Command("Avbryt", Command.BACK, 1);
        editWebsiteSaveCommand = new Command("Spara", Command.OK, 2);

        editWebSiteForm.addCommand(editWebSiteBackCommand);
        editWebSiteForm.addCommand(editWebSiteCancelCommand);
        editWebSiteForm.addCommand(editWebsiteSaveCommand);
        editWebSiteForm.setCommandListener(this);




        //--------------- Alert-Screen -----------------------------------------

        alertEditSettings = new Alert("Sparar Ändringar",
                                      "\n\n\n...Ändringar sparas... ",
                                      null, AlertType.CONFIRMATION);
        setDataStore();
        upDateDataStore();
        alertEditSettings.setTimeout(2000);

        //------------- Dial -----------------------------------------

        dialTextBox = new TextBox("Ringa nummer", "", 30, TextField.PHONENUMBER);

        DialCommand = new Command("Dial", Command.OK, 0);
        meny = new Command("H-meny", Command.BACK, 1);
        minimazeCommand = new Command("Minimera", Command.SCREEN, 1);

        dialTextBox.addCommand(meny);
        dialTextBox.addCommand(minimazeCommand);
        dialTextBox.addCommand(DialCommand);
        dialTextBox.setCommandListener(this);

        //------------ koppla vidare samtal ------------------------------------

        connectTextBox = new TextBox("Koppla samtal", "", 30,
                                     TextField.PHONENUMBER);

        ConnectDialCommand = new Command("Koppla", Command.OK, 0);
        ConnectDialBackCommand = new Command("Bakåt", Command.BACK, 1);

        connectTextBox.addCommand(ConnectDialCommand);
        connectTextBox.addCommand(ConnectDialBackCommand);
        connectTextBox.setCommandListener(this);

        // ------ fristående kommandon -----------------------------------------

        AboutCommand = new Command("Om Mobisma", Command.HELP, 1);
        goBackCommand = new Command("Bakåt", Command.BACK, 0);
        helpCommand = new Command("Hjälp", Command.HELP, 4);
        urlCommand = new Command("URL", Command.OK, 1);

        //------------ kontrollerar datumet för demo-lisencer-------------------

        /* controllString();
         controllDate();
         this.ViewDateString = setViewDateString();*/

        if (ViewDateString == null) {
            this.ViewDateString = "Enterprise License";

        }

    }
    public void openWAP() {

        try {
            platformRequest(URL); // Öppna wappsidan.
            Display.getDisplay(this).setCurrent(dialTextBox);
            Display.getDisplay(this).setCurrent(null);
        } catch (ConnectionNotFoundException ex) {
        }

    }

    public int getGameAction(int i) {
        int checkNumber = i;
        if (checkNumber == 1) {
            return 1;
        }
        return 0;
    }

    public List getSettingsList() {

        return nationell_List;
    }

    public List getCallList() {

        return callList;
    }


    public String sortCharAt(String s) {

        this.sortString = identy; // sortString innehåller samma som för IMEI-strängen för att kunna kontrollera å sortera bort tecken....

        StringBuffer bTecken = new StringBuffer(sortString); // Lägg strängen sortString i ett stringbuffer objekt...

        for (int i = 0; i < bTecken.length(); i++) { // räkna upp hela bTecken-strängens innehåll hela dess längd

            char tecken = bTecken.charAt(i); // char tecken är innehållet i hela längden

            if ('A' <= tecken && tecken <= 'Z' ||
                'a' <= tecken && tecken <= 'z' // Sorterar ur tecken ur IMEI-strängen
                || tecken == '-' || tecken == '/' || tecken == '\\' ||
                tecken == ':' || tecken == ';'
                || tecken == '.' || tecken == ',' || tecken == '_' ||
                tecken == '|' || tecken == '<'
                || tecken == '>' || tecken == '+' || tecken == '(' ||
                tecken == ')') {

                bTecken.setCharAt(i, ' '); // lägg in blanksteg i IMEI-strängen där något av ovanstående tecken finns....
            }

        }

        bTecken.append(' '); // lägger till blanksteg sist i raden så att sista kommer med för att do-satsen ska kunna hitta och sortera...

        String setString = new String(bTecken); // Gör om char-strängen till en string-sträng

        int antal = 0;
        char separator = ' '; // för att kunna sortera i do-satsen

        int index = 0;

        do { // do satsen sorterar ut blankstegen och gör en ny sträng för att jämföra IMEI med...
            ++antal;
            ++index;

            index = setString.indexOf(separator, index);
        } while (index != -1);

        subStr = new String[antal];
        index = 0;
        int slutindex = 0;

        for (int j = 0; j < antal; j++) {

            slutindex = setString.indexOf(separator, index);

            if (slutindex == -1) {
                subStr[j] = setString.substring(index);
            }

            else {
                subStr[j] = setString.substring(index, slutindex);
            }

            index = slutindex + 1;

        }
        String setNumber = "";
        for (int k = 0; k < subStr.length; k++) {

            setNumber += subStr[k]; // Lägg in värdena från subStr[k] i strängen setNumber....
        }

        System.out.println("Sorterad: " + setNumber);

        System.out.println("" + identy);

        String sendIMEI = setNumber;

        return sendIMEI;
    }

    public String toString(String b) {

        String s = b;

        return s;
    }

    public void startApp() {

        try {
            setDataStore();
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (RecordStoreException ex) {
        }
        try {
            upDateDataStore();
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            controllString();
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreNotOpenException ex2) {
        } catch (RecordStoreException ex2) {
        }

        Display.getDisplay(this).setCurrent(mainList);
    }

    public void pauseApp() {

    }

    public void destroyApp(boolean unconditional) {

    }

    public void checkCountryNumber1() { // Justerar landsiffra som är inmatad! Tar bort '+' och lägger in '00' före landssiffran

        String larmNummer = "112";
        String Number = "+";
        String setNumber = "00";
        String validate = connectTextBox.getString();
        String validate46 = "46";
        String setNumberNoll = "0";

        if (Number.equals(validate.substring(0, 1)) &&
            validate46.equals(validate.substring(1, 3))) { // Om numret startar med '+' OCH '46' är sann så gör om till '0'

            accessCode = accessNumber;

            System.out.println("+46 är SANN gör om till 0 ");

            String setString = connectTextBox.getString();

            String deletePartOfString = setString.substring(3); // ta bort plast 0 - 1 ur strängen....

            String setStringTotal = setNumberNoll + deletePartOfString; // sätt ihop strängen setStringTotal

            stringTotal = setStringTotal;

            this.stringTotal = stringTotal + "#";

            System.out.println("Landsnummer är : " + stringTotal);

        }
        if (Number.equals(validate.substring(0, 1)) &&
            !validate46.equals(validate.substring(1, 3))) { // Om numret startar med '+' OCH 46 är falsk så gör om till '00'

            accessCode = accessNumber;

            System.out.println("Andra landsnummer tex +47 blir 00 SANN");

            String setString = connectTextBox.getString();

            String deletePartOfString = setString.substring(1); // ta bort plast 0 - 1 ur strängen....

            String setStringTotal = setNumber + deletePartOfString; // sätt ihop strängen setStringTotal

            stringTotal = setStringTotal;

            this.stringTotal = stringTotal + "#";

            System.out.println("Landsnummer: " + stringTotal);

        }
        if (!Number.equals(validate.substring(0, 1))) { // ring vanligt nummer

            accessCode = accessNumber;

            this.stringTotal = connectTextBox.getString();

            System.out.println("Telefonnummer: " + stringTotal);

        }
        if (validate.equals(validate.substring(0, 1)) ||
            validate.equals(validate.substring(0, 2)) ||
            validate.equals(validate.substring(0, 3)) ||
            validate.equals(validate.substring(0, 4))) {

            accessCode = "";

            this.stringTotal = connectTextBox.getString();

            System.out.println("Internnummer: " + stringTotal);
        }
    }

    public void checkCountryNumber() { // Justerar landsiffra som är inmatad! Tar bort '+' och lägger in '00' före landssiffran

        String larmNummer = "112";
        String Number = "+";
        String setNumber = "00";
        String validate = dialTextBox.getString();
        String validate46 = "46";
        String setNumberNoll = "0";

        if (Number.equals(validate.substring(0, 1)) &&
            validate46.equals(validate.substring(1, 3))) { // Om numret startar med '+' OCH '46' är sann så gör om till '0'

            accessCode = accessNumber;

            System.out.println("+46 är SANN gör om till 0 ");

            String setString = dialTextBox.getString();

            String deletePartOfString = setString.substring(3); // ta bort plast 0 - 1 ur strängen....

            String setStringTotal = setNumberNoll + deletePartOfString; // sätt ihop strängen setStringTotal

            stringTotal = setStringTotal;

            this.stringTotal = stringTotal;

            System.out.println("Landsnummer är : " + stringTotal);

        }
        if (Number.equals(validate.substring(0, 1)) &&
            !validate46.equals(validate.substring(1, 3))) { // Om numret startar med '+' OCH 46 är falsk så gör om till '00'

            accessCode = accessNumber;

            System.out.println("Andra landsnummer tex +47 blir 00 SANN");

            String setString = dialTextBox.getString();

            String deletePartOfString = setString.substring(1); // ta bort plats 0 - 1 ur strängen....

            String setStringTotal = setNumber + deletePartOfString; // sätt ihop strängen setStringTotal

            stringTotal = setStringTotal;

            this.stringTotal = stringTotal;

            System.out.println("Landsnummer: " + stringTotal);

        }
        if (!Number.equals(validate.substring(0, 1))) { // ring vanligt nummer

            accessCode = accessNumber;

            this.stringTotal = dialTextBox.getString();

            System.out.println("Telefonnummer: " + stringTotal);

        }
        if (validate.equals(validate.substring(0, 1)) ||
            validate.equals(validate.substring(0, 2)) ||
            validate.equals(validate.substring(0, 3)) ||
            validate.equals(validate.substring(0, 4))) {

            accessCode = "";

            this.stringTotal = dialTextBox.getString();

            System.out.println("Internnummer: " + stringTotal);
        }
    }

    public void commandAction(Command c, Displayable d) { // SÄTTER COMMAND-ACTION STARTAR TRÄDETS KOMMANDON (trådar)
        Thread th = new Thread(this);
        thCmd = c;
        th.start();
        if (d.equals(mainList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(mainList)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                       try {
                            platformRequest(domain); // Öppna wappsidan.
                        } catch (ConnectionNotFoundException ex4) {
                        }
                        Display.getDisplay(this).setCurrent(mainList);



                        break;

                    case 1:

                        Display.getDisplay(this).setCurrent(dialTextBox);

                        break;
                    case 2:
                        genNameScr("Uppringda", null, null, true);

                        //genEntryScr();

                        break;

                    case 3:
                        Display.getDisplay(this).setCurrent(null);
                        break;

                    }
                }

            }

        }
        if (d.equals(nationell_List)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(nationell_List)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:
                        nationell = "3";

                        openRecStore();
                        setNationellSettings(nationell);
                        closeRecStore();
                        try {
                            upDateDataStore();
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreException ex) {
                        }

                        try {
                            System.out.println(
                                    getSwitchBoardType());
                        } catch (RecordStoreNotOpenException
                                 ex2) {
                        } catch (InvalidRecordIDException ex2) {
                        } catch (RecordStoreException ex2) {
                        }
                        Display.getDisplay(this).setCurrent(getEditSettingForm());

                        break;

                    case 1:
                        internationell = "4";

                        openRecStore();
                        setInternationellSettings(internationell);
                        closeRecStore();
                        try {
                            upDateDataStore();
                        } catch (RecordStoreNotOpenException
                                 ex3) {
                        } catch (InvalidRecordIDException ex3) {
                        } catch (RecordStoreException ex3) {
                        }

                        try {
                            System.out.println(
                                    getSwitchBoardType());
                        } catch (RecordStoreNotOpenException
                                 ex1) {
                        } catch (InvalidRecordIDException ex1) {
                        } catch (RecordStoreException ex1) {
                        }
                        Display.getDisplay(this).setCurrent(getEditSettingForm());
                        break;

                    }
                }

            }

        }

    }

    public void run() {
        try {
            if (thCmd.getCommandType() == Command.EXIT) {
                notifyDestroyed();
            } else if (thCmd == AboutCommand) { // Kommandot 'Om Tv-Moble' hör till huvudfönstret listan

                backCommand = new Command("Bakåt", Command.OK, 2);

                Displayable k = new AboutUs();
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(backCommand);
                k.setCommandListener(this); // settingsListNextCommand

            } else if (thCmd == helpCommand) { // Kommandot 'Om Tv-Moble' hör till huvudfönstret listan

                backCommand = new Command("Bakåt", Command.OK, 2);

                Displayable k = new HelpInfo();
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(backCommand);
                k.setCommandListener(this);

            }  else if (thCmd == emptyCallListCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(alertEditSettings, mainList);
                try {
                        deleteAllRecords();
                    } catch (RecordStoreException ex4) {
                    }


            }else if (thCmd == urlCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(getEditWebSiteForm());

            }else if (thCmd == propertiesCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(getSettingsList());

            } else if (thCmd == settingsListCancelCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(mainList);

            } else if (thCmd == meny) {

                Display.getDisplay(this).setCurrent(mainList);

            } else if (thCmd == DialCallListCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(getCallList());

            } else if (thCmd == cmdBack) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(mainList);

            } else if (thCmd == ConnectCommand) { // Kommandot 'Redigera' hör till setting-Form

                //Display.getDisplay(this).setCurrent(connectTextBox);
                try {
                    platformRequest(URL); // Öppna wappsidan.
                    Display.getDisplay(this).setCurrent(dialTextBox);
                    Display.getDisplay(this).setCurrent(null);
                } catch (ConnectionNotFoundException ex) {
                }

            } else if (thCmd == cmdDial) {
                // dial the phone screen
                genDialScr();
            }

            // Handle the name entry screen
            else if (thCmd == cmdCancel) {
                // display main screen
                genMainScr();
            } else if (thCmd == cmdAdd) {
                // display name entry screen
                addEntry();
            }

            // Handle the option screen
            else if (thCmd == cmdBack) {
                // display main screen
                genMainScr();
            }

            // Handle the search screen
            else if (thCmd == cmdBack) {
                // display main screen
                genMainScr();
            } else if (thCmd == cmdSearchNetwork || thCmd == cmdSearchLocal) {

                // display search of local addr book
                genNameScr("Search Result", s_firstName.getString(), s_lastName
                           .getString(), thCmd == cmdSearchLocal);
            }

            else if (thCmd == cmdCancel) {
                // display main screen
                genMainScr();
            }

            else if (thCmd == ConnectDialBackCommand) { // Kommandot 'Redigera' hör till setting-Form

                Display.getDisplay(this).setCurrent(dialTextBox);

            } else if (thCmd == settingsCommand) {

                propertiesCommand = new Command("Redigera", Command.OK, 3);
                setDataStore();
                upDateDataStore();

                Displayable k = new ServerNumber(switchBoardNumber, /*, IMEI,
                                                 star,*/
                                                 accessNumber, ViewDateString);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(propertiesCommand);
                k.addCommand(goBackCommand);
                k.addCommand(urlCommand);
                k.addCommand(AboutCommand);
                k.addCommand(helpCommand);
                k.setCommandListener(this);

            } else if (thCmd == editWebSiteBackCommand) { // Kommandot 'Tillbaka' hör till editSetting-Form

                propertiesCommand = new Command("Redigera", Command.OK, 3);
                setDataStore();
                upDateDataStore();

                Displayable k = new ServerNumber(switchBoardNumber, /*, IMEI,
                                                 star,*/
                                                 accessNumber, ViewDateString);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(propertiesCommand);
                k.addCommand(goBackCommand);
                k.addCommand(urlCommand);
                k.addCommand(AboutCommand);
                k.addCommand(helpCommand);
                k.setCommandListener(this);

            }else if (thCmd == editSettingBackCommand) { // Kommandot 'Tillbaka' hör till editSetting-Form

                propertiesCommand = new Command("Redigera", Command.OK, 3);
                setDataStore();
                upDateDataStore();

                Displayable k = new ServerNumber(switchBoardNumber, /*, IMEI,
                                                 star,*/
                                                 accessNumber, ViewDateString);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(propertiesCommand);
                k.addCommand(goBackCommand);
                k.addCommand(urlCommand);
                k.addCommand(AboutCommand);
                k.addCommand(helpCommand);
                k.setCommandListener(this);

            } else if (thCmd == backCommand) { // Kommandot 'Tillbaka' hör till about-formen

                propertiesCommand = new Command("Redigera", Command.OK, 3);
                setDataStore();
                upDateDataStore();

                Displayable k = new ServerNumber(switchBoardNumber, /*, IMEI,
                                                 star,*/
                                                 accessNumber, ViewDateString);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(propertiesCommand);
                k.addCommand(goBackCommand);
                k.addCommand(urlCommand);
                k.addCommand(AboutCommand);
                k.addCommand(helpCommand);
                k.setCommandListener(this);

            } else if (thCmd == backCommand) { // Kommandot 'Tillbaka' hör till about-formen

                propertiesCommand = new Command("Redigera", Command.OK, 3);
                setDataStore();
                upDateDataStore();

                Displayable k = new ServerNumber(switchBoardNumber, /*, IMEI,
                                                 star,*/
                                                 accessNumber, ViewDateString);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(propertiesCommand);
                k.addCommand(goBackCommand);
                k.addCommand(urlCommand);
                k.addCommand(AboutCommand);
                k.addCommand(helpCommand);
                k.setCommandListener(this);

            } else if (thCmd == editWebSiteCancelCommand) { // Kommandot 'Logga Ut' hör till setting-Form

                Display.getDisplay(this).setCurrent(mainList);

            }else if (thCmd == editSettingCancelCommand) { // Kommandot 'Logga Ut' hör till setting-Form

                Display.getDisplay(this).setCurrent(mainList);

            } else if (thCmd == editSettingSaveCommand) { // Kommandot 'Spara' hör till editSetting-Form

                openRecStore();
                setAccessNumber();
                setSwitchBoardNumber();
                setExtensionNumber();
                setPinCodeNumber();
                closeRecStore();
                upDateDataStore();
                startApp();

                Display.getDisplay(this).setCurrent(alertEditSettings,
                        mainList);

            }else if (thCmd == editWebsiteSaveCommand) { // Kommandot 'Spara' hör till editSetting-Form

                openRecStore();
                setURL();
                closeRecStore();
                upDateDataStore();
                startApp();

                Display.getDisplay(this).setCurrent(alertEditSettings,
                        mainList);

            } else if (thCmd == minimazeCommand) {

                Display.getDisplay(this).setCurrent(null);

            } else if (thCmd.getCommandType() == Command.BACK) { // Kommandot 'Tillbaka' hör till about-formen

                Display.getDisplay(this).setCurrent(mainList);
            } else if (thCmd == DialCallListCommand) {
                if (type == 0) {
                    try {
                        checkCountryNumber();
                        if (SOS.equals(stringTotal)) {
                            platformRequest("tel:" + stringTotal.trim());
                        } else if (switchboardType.equals("3")) { // nationell växeltyp
                            platformRequest("tel:" + switchBoardNumber + setP +
                                            extensionNumber + "*" +
                                            pinCodeNumber + "#"
                                            + accessCode + mainCallString); // dial the number > DTMF-signals.
                        } else if (switchboardType.equals("4")) { // internationell växeltyp
                            platformRequest("tel:" + switchBoardNumber + setP +
                                            extensionNumber + "*" +
                                            pinCodeNumber + "#"
                                            + accessCode + mainCallString); // dial the number > DTMF-signals.
                        }
                        dialTextBox.setString("");
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        platformRequest(dialTextBox.getString()); // open the wap browser.
                    } catch (Exception e) {
                    }
                }
            } else if (thCmd == DialCommand) {
                if (type == 0) {
                    try {

                        checkCountryNumber();
                        String checkSwitchboardNumber;
                        openRecStore();
                        checkSwitchboardNumber = getSwitchBoardType();
                        closeRecStore();
                        System.out.println(checkSwitchboardNumber);

                        if (SOS.equals(stringTotal)) {
                            platformRequest("tel:" + stringTotal.trim());
                        } else if (checkSwitchboardNumber.equals("3")) { // nationell växeltyp

                        platformRequest("tel:" + switchBoardNumber + setP +
                                            accessCode + stringTotal.trim()); // dial the number > DTMF-signals.


                        } else if (checkSwitchboardNumber.equals("4")) { // internationell växeltyp

                        platformRequest("tel:" + switchBoardNumber + setP +
                                            extensionNumber + "*" +
                                            pinCodeNumber + "#" + accessCode +
                                            stringTotal.trim()); // dial the number > DTMF-signals.

                        }
                        String dialedNumber = stringTotal;
                        saveDialedNumber(dialedNumber);
                        dialTextBox.setString("");
                        getTime();
                        //Display.getDisplay(this).setCurrent(null);
                        //Display.getDisplay(this).setCurrent(mainList);

                    }  catch (Exception e) {
                    }
                } else {
                    try {
                        platformRequest(dialTextBox.getString()); // open the wap browser.
                    } catch (Exception e) {
                    }
                }
            }

        } catch (Exception ex) {
        }
    }

    public String getTime(){



        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int year = cal.get(Calendar.YEAR);
        int mounth = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String sub = year + "";

        String years = sub.substring(2);

       String time = hour + "." + minute + " " + day + "/" + mounth + "-" + years;

       System.out.println("klockan är >> " + time);

       return time;


    }

    public void saveDialedNumber(String dialedNumber) throws
            RecordStoreNotOpenException, RecordStoreException {


        String time = getTime();
        String emptyString = "";

        byte[] b = SimpleRecord.createRecord(time, emptyString, dialedNumber);
        addrBook.addRecord(b, 0, b.length);

    }

    public void deleteAllRecords() throws RecordStoreException {


        RecordStore rs = null;
    RecordEnumeration re = null;
    try {
        rs = RecordStore.openRecordStore("TheAddressBook", true);
        re = rs.enumerateRecords(null, null, false);

        // First remove all records, a little clumsy.
        while (re.hasNextElement()) {
            int id = re.nextRecordId();
            rs.deleteRecord(id);
        }
    }catch (Exception ex) {
        }

    }



    //------------ D A T A - B A S - R M S -----------------------------------

    public Form getEditSettingForm() { // METODEN RETURNERAR FORMEN FÖR EDITSETTINGS I EGENSKAPER

        editSettingForm.deleteAll();
        openRecStore();
        accessNumbers.setString(accessNumber);
        editSettingForm.append(accessNumbers);
        editSwitchBoardNumber.setString(switchBoardNumber);
        editSettingForm.append(editSwitchBoardNumber);
        editExtensionNumber.setString(extensionNumber);
        editSettingForm.append(editExtensionNumber);
        editPinCodeNumber.setString(pinCodeNumber);
        editSettingForm.append(editPinCodeNumber);
        closeRecStore();

        return editSettingForm;
    }

    public Form getEditWebSiteForm() { // METODEN RETURNERAR FORMEN FÖR EDITSETTINGS I EGENSKAPER

        editWebSiteForm.deleteAll();
        openRecStore();
        editWebSite.setString(domain);
        editWebSiteForm.append(editWebSite);
        closeRecStore();

        return editWebSiteForm;
    }


    // --- SET-metoder ------



    public void setDateNumber() {

        try {
            recStore.setRecord(3, dateNumber.getString().getBytes(), 0,
                               dateNumber.getString().length());
        } catch (Exception e) {
            // ALERT
        }
    }

    public void setAccessNumber() {

        try {
            recStore.setRecord(4, accessNumbers.getString().getBytes(), 0,
                               accessNumbers.getString().length());
        } catch (Exception e) {
            // ALERT
        }
    }

    public void setSwitchBoardNumber() {
        try {
            recStore.setRecord(5, editSwitchBoardNumber.getString().getBytes(),
                               0,
                               editSwitchBoardNumber.getString().length());
        } catch (Exception e) {
            // ALERT
        }
    }

    public void setExtensionNumber() {
        try {
            recStore.setRecord(9, editExtensionNumber.getString().getBytes(),
                               0,
                               editExtensionNumber.getString().length());
        } catch (Exception e) {
            // ALERT
        }
    }

    public void setPinCodeNumber() {
        try {
            recStore.setRecord(10, editPinCodeNumber.getString().getBytes(),
                               0,
                               editPinCodeNumber.getString().length());
        } catch (Exception e) {
            // ALERT
        }
    }

    public void setNationellSettings(String nationellstring) {

        String setNewNationellSring = nationellstring;

        try {
            recStore.setRecord(11, setNewNationellSring.getBytes(),
                               0,
                               setNewNationellSring.length());
        } catch (Exception e) {
            // ALERT
        }
    }


    public void setInternationellSettings(String internationellstring) {

        String setNewInternationellString = internationellstring;

        try {
            recStore.setRecord(11, setNewInternationellString.getBytes(),
                               0,
                               setNewInternationellString.length());
        } catch (Exception e) {
            // ALERT
        }
    }
    public void setURL() {

        try {
             recStore.setRecord(12, editWebSite.getString().getBytes(),
                                0,
                                editWebSite.getString().length());
         } catch (Exception e) {
             // ALERT
         }

    }


    public void setTWO() { // skiver in i första lediga plats i databasen.. tex. om db 1 - 9 är upptagna skriver metoden in på plats 10...
        try {

            openRecStore();
            String appt = "2";
            byte bytes[] = appt.getBytes();
            recStore.addRecord(bytes, 0, bytes.length);

            closeRecStore();
            upDateDataStore();
            startApp();

        } catch (Exception e) {
            // ALERT
        }
    }


    // ---- GET-metoder ---------

    public String getYear() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte a[] = recStore.getRecord(1);
        setYear = new String(a, 0, a.length);

        closeRecStore();

        return setYear;

    }

    public String getMounth() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte b[] = recStore.getRecord(2);
        setMounth = new String(b, 0, b.length);

        closeRecStore();

        return setMounth;

    }

    public String getDate() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte c[] = recStore.getRecord(3);
        setDate = new String(c, 0, c.length);

        closeRecStore();

        return setDate;

    }

    public String getAccessNumber() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte d[] = recStore.getRecord(4);
        accessNumber = new String(d, 0, d.length);

        closeRecStore();

        return accessNumber;

    }


    public String getSwitchBoardNumber() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte e[] = recStore.getRecord(5);
        switchBoardNumber = new String(e, 0, e.length);

        closeRecStore();

        return switchBoardNumber;

    }

    public String getThisYearBack() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte f[] = recStore.getRecord(6);
        setyearBack = new String(f, 0, f.length);

        closeRecStore();

        return setyearBack;

    }

    public String getThisMounthBack() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte g[] = recStore.getRecord(7);
        setmounthBack = new String(g, 0, g.length);

        closeRecStore();

        return setmounthBack;

    }

    public String getThisDayBack() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte h[] = recStore.getRecord(8);
        setdayBack = new String(h, 0, h.length);

        closeRecStore();

        return setdayBack;

    }

    public void getTWO() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();
        readRecords();
        readRecordsUpdate();

        try {
            byte i[] = recStore.getRecord(17);
            getTWO = new String(i, 0, i.length);
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (RecordStoreException ex) {
        }

        try {
            this.dateString = getTWO;
        } catch (Exception ex1) {
        }

        System.out.println("häääääääääärrrrrrrr >>> getTWO >> " + getTWO);
        closeRecStore();

    }

    public String getExtensionNumber() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte j[] = recStore.getRecord(9);
        extensionNumber = new String(j, 0, j.length);

        closeRecStore();

        return extensionNumber;

    }

    public String getPinCodeNumber() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();

        byte k[] = recStore.getRecord(10);
        pinCodeNumber = new String(k, 0, k.length);

        closeRecStore();

        return pinCodeNumber;

    }

    public String getSwitchBoardType() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        String switchboards = "";

        openRecStore();

        byte l[] = recStore.getRecord(11);
        switchboards = new String(l, 0, l.length);

        closeRecStore();

        return switchboards;

    }

    public String getURL() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        String url = "";

        openRecStore();

        byte a1[] = recStore.getRecord(12);
        url = new String(a1, 0, a1.length);

        closeRecStore();

        return url;

    }

    public void readRecordsUpdate() {
        try {
            System.out.println("Number of records: " + recStore.getNumRecords());

            if (recStore.getNumRecords() > 0) {
                RecordEnumeration re = recStore.enumerateRecords(null, null, false);
                while (re.hasNextElement()) {
                    String str = new String(re.nextRecord());
                    System.out.println("Record: " + str);
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void readRecords() {
        try {
            // Intentionally small to test code below
            byte[] recData = new byte[5];
            int len;

            for (int i = 1; i <= recStore.getNumRecords(); i++) {
                // Allocate more storage if necessary
                if (recStore.getRecordSize(i) > recData.length) {
                    recData = new byte[recStore.getRecordSize(i)];
                }

                len = recStore.getRecord(i, recData, 0);
                if (Settings.debug) {
                    System.out.println("Record ID#" + i + ": " +
                                       new String(recData, 0, len));
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void writeRecord(String str) {
        byte[] rec = str.getBytes();

        try {
            System.out.println("sparar ");
            recStore.addRecord(rec, 0, rec.length);
            System.out.println("Writing record: " + str);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }


    public void openRecStore() {
        try {
            System.out.println("Öppnar databasen");
            // The second parameter indicates that the record store
            // should be created if it does not exist
            recStore = RecordStore.openRecordStore(REC_STORE, true);

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void closeRecStore() {
        try {
            recStore.closeRecordStore();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void setDataStore() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreNotOpenException,
            RecordStoreException {

        openRecStore();
        readRecords();
        readRecordsUpdate();

        if (recStore.getNumRecords() == 0) { // om innehållet i databasen är '0' så spara de tre första elementen i databasen.

            writeRecord(setYear);
            writeRecord(setMounth);
            writeRecord(setDate);
            writeRecord("0"); // Accessnummer 4
            writeRecord("0"); // Växelnummer  5
            writeRecord(setyearBack);
            writeRecord(setmounthBack);
            writeRecord(setdayBack);
            writeRecord("0"); // Anknytningsnummer 9
            writeRecord("0"); // Pinkod    10
            writeRecord("0"); // Pekar på om det är en nationell (3) eller en internationel (4) växel plats 11.
            writeRecord("http://www.mobisma.com/wap/default.wml"); // wml-adress plats 12
            writeRecord("0"); // Senast slagna nummer plats 13
            writeRecord("0"); // Senast slagna nummer plats 14
            writeRecord("0"); // Senast slagna nummer plats 15
            writeRecord("0"); // Senast slagna nummer plats 16

        }

        // sätter nummer i fönstret under inställningar...

        byte d[] = recStore.getRecord(4);
        accessNumber = new String(d, 0, d.length);

        byte e[] = recStore.getRecord(5);
        switchBoardNumber = new String(e, 0, e.length);

        byte j[] = recStore.getRecord(9);
        extensionNumber = new String(j, 0, j.length);

        byte k[] = recStore.getRecord(10);
        pinCodeNumber = new String(k, 0, k.length);

        byte l[] = recStore.getRecord(11);
        nationell = new String(l, 0, l.length);

        byte m[] = recStore.getRecord(12);
        domain = new String(m, 0, m.length);

        closeRecStore();
    }

    // Om något inputfönster(post) i databasen är tom sätt tillbaka värdet...
    // Det finns totalt 15 olika 'sätt' som databasen kan ha tomma poster med 4 värden.
    // Stämmer med den diskreta matematiken enligt KTH ;-)
    public void upDateDataStore() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        openRecStore();
        String setBackAccessNumberRecord = accessNumber; // databas plats post = 4.
        String setBackSwitchBoardNumberRecord = switchBoardNumber; // databas plats post = 5.
        String setBackExtensionNumberRecord = extensionNumber; // databas plats post = 9.
        String setBackPinCodeNumberRecord = pinCodeNumber; // databas plats post = 10.
        String setBackURLRecord = domain;

        if (recStore.getRecord(4) == null && recStore.getRecord(5) == null && // FAll 1 alla 4 poster är tomma. spar in alla tomma värden igen.
            recStore.getRecord(9) == null && recStore.getRecord(10) == null) {

            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(5) == null &&
                   recStore.getRecord(9) == null) {

            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(9) == null &&
                   recStore.getRecord(10) == null) {

            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(5) == null &&
                   recStore.getRecord(10) == null) {
            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(5) == null && recStore.getRecord(9) == null &&
                   recStore.getRecord(10) == null) {
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(5) == null) {
            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(9) == null) {
            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());

        } else if (recStore.getRecord(4) == null && recStore.getRecord(10) == null) {
            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(5) == null && recStore.getRecord(10) == null) {
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(5) == null && recStore.getRecord(9) == null) {
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());

        } else if (recStore.getRecord(9) == null && recStore.getRecord(10) == null) {
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        } else if (recStore.getRecord(4) == null) {
            recStore.setRecord(4, setBackAccessNumberRecord.getBytes(), 0,
                               setBackAccessNumberRecord.length());

        } else if (recStore.getRecord(5) == null) {
            recStore.setRecord(5, setBackSwitchBoardNumberRecord.getBytes(), 0,
                               setBackSwitchBoardNumberRecord.length());

        } else if (recStore.getRecord(9) == null) {
            recStore.setRecord(9, setBackExtensionNumberRecord.getBytes(), 0,
                               setBackExtensionNumberRecord.length());

        } else if (recStore.getRecord(10) == null) {
            recStore.setRecord(10, setBackPinCodeNumberRecord.getBytes(), 0,
                               setBackPinCodeNumberRecord.length());

        }else if (recStore.getRecord(12) == null) {
            recStore.setRecord(12, setBackURLRecord.getBytes(), 0,
                               setBackURLRecord.length());

        }


        closeRecStore();
    }


// ------------------- D A T U M -----------------------------------------------

    public void controllString() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        String readRecord;

        getTWO(); // ställ om i databasen så att kontrollen står mot rätt databaspost.

        readRecord = dateString;

        String viewRecord = readRecord;

        try {
            if (viewRecord.equals("2")) {

                notifyDestroyed();
            }
        } catch (Exception ex) {
        }
        System.out.println("VÄRDET PLATS DB >> " + viewRecord);
    }

    public void controllDate() throws IOException, RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        try {
            this.DBdate = getDate();
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        try {
            this.DBmounth = getMounth();
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            this.DByear = getYear();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        try {
            this.DBdateBack = getThisDayBack();
        } catch (RecordStoreNotOpenException ex3) {
        } catch (InvalidRecordIDException ex3) {
        } catch (RecordStoreException ex3) {
        }
        try {
            this.DBmounthBack = getThisMounthBack();
        } catch (RecordStoreNotOpenException ex4) {
        } catch (InvalidRecordIDException ex4) {
        } catch (RecordStoreException ex4) {
        }
        try {
            this.DByearBack = getThisYearBack();
        } catch (RecordStoreNotOpenException ex5) {
        } catch (InvalidRecordIDException ex5) {
        } catch (RecordStoreException ex5) {
        }

        String useDBdate = DBdate.trim();
        String useDBmounth = DBmounth.trim();
        String useDByear = DByear.trim();

        String useDBdateBack = DBdateBack.trim();
        String useDBmounthBack = DBmounthBack.trim();
        String useDByearBack = DByearBack.trim();

        System.out.println("Skriver ut datum om 30 dagar >>> " + useDBdate);
        System.out.println("Skriver ut månad om 30 dagar >>> " + useDBmounth);
        System.out.println("Skriver ut året om 30 dagar >>> " + useDByear);

        System.out.println("Skriver ut Kontroll datum >>> " + useDBdateBack);
        System.out.println("Skriver ut Kontroll månad >>> " + useDBmounthBack);
        System.out.println("Skriver ut Kontroll år >>> " + useDByearBack);

        String toDayDate = checkDay().trim();
        String toDayMounth = checkMounth().trim();

        System.out.println("Skriver ut DAGENS DATUM >>> " + toDayDate);
        System.out.println("Skriver ut ÅRETS MÅNAD >>> " + toDayMounth);

        Integer controllDBdateBack = new Integer(0); // Gör om strängar till integer
        Integer controllDBmonthBack = new Integer(0); // Gör om strängar till integer
        Integer controllDByearBack = new Integer(0); // Gör om strängar till integer

        int INTDBdateBack = controllDBdateBack.parseInt(useDBdateBack);
        int INTDBmounthBack = controllDBmonthBack.parseInt(DBmounthBack);
        int INTDByearBack = controllDByearBack.parseInt(DByearBack);

        Integer controllDBdate = new Integer(0); // Gör om strängar till integer
        Integer controllDBmonth = new Integer(0); // Gör om strängar till integer
        Integer controllDByear = new Integer(0); // Gör om strängar till integer

        Integer controllToDayDBdate = new Integer(0); // Gör om strängar till integer
        Integer controllToDayDBmounth = new Integer(0); // Gör om strängar till integer

        int INTDBdate = controllDBdate.parseInt(useDBdate);
        int INTDBmounth = controllDBmonth.parseInt(DBmounth);
        int INTDByear = controllDByear.parseInt(DByear);

        int INTdateToDay = controllToDayDBdate.parseInt(toDayDate);
        int INTmounthToDay = controllToDayDBmounth.parseInt(toDayMounth);

        if (INTDBdate <= INTdateToDay && INTDBmounth <= INTmounthToDay &&
            INTDByear == checkYear) {

            System.out.println("SANN SANN SANN SANN SANN ");

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }
        if (INTmounthToDay == 0) { // Om INTmounthToDay har värdet '0' som är januari

            INTDBmounthBack = 0; // Då innehåller installations-månaden samma värde som nu-månaden.

        }
        if (INTDBmounthBack > INTmounthToDay) { // Om installations-månaden är större än 'dagens' månad som är satt i mobilen så stäng...

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }
        if (INTDBmounthBack > INTmounthToDay && INTDByearBack < checkYear) { // Om installations-månaden är större än 'dagens' månad som är satt i mobilen så stäng...

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }
        if (INTDByearBack > checkYear) { // Om installations-året är större än året som är satt i mobilen. >> går bakåt i tiden...

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }
        if (INTDBdateBack > INTdateToDay && INTDBmounthBack > INTmounthToDay &&
            INTDByearBack > checkYear) {

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }
        if (INTDBmounthBack > INTmounthToDay && INTDByearBack > checkYear) {

            setTWO(); // Om månad och datum är sann skriv in "2" i databasen plats 10...

        }

    }


    public void setDBDate() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        countDay();

        System.out.println("Om 30 dagar är det den >> " + dayAfter +
                           ", och månad >> " + monthAfter + " det är år >> " +
                           yearAfter);

        String convertDayAfter = Integer.toString(dayAfter); // konvertera int till string...
        String convertMounthAfter = Integer.toString(monthAfter);
        String convertYearAfter = Integer.toString(yearAfter);

        this.setDate = convertDayAfter;
        this.setMounth = convertMounthAfter;
        this.setYear = convertYearAfter;

    }

    public void setDBDateBack() {

        countThisDay();

        System.out.println("Kontrollerar dagens dautm >> " + dayBack +
                           ", och månad >> " + mounthBack + " det är år >> " +
                           yearBack);

        String convertDayBack = Integer.toString(dayBack); // konvertera int till string...
        String convertMounthBack = Integer.toString(mounthBack);
        String convertYearBack = Integer.toString(yearBack);

        this.setdayBack = convertDayBack;
        this.setmounthBack = convertMounthBack;
        this.setyearBack = convertYearBack;

    }

    public void countThisDay() {

        // Get today's day and month
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        System.out.println("Dagens datum är den >> " + day +
                           ", Årets månad är nummer >> " + month +
                           " det är år >> " + year);

        this.dayBack = day;
        this.mounthBack = month;
        this.yearBack = year;

    }

    public void countDay() {

        // Get today's day and month
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        System.out.println("Dagens datum är den >> " + day +
                           ", Årets månad är nummer >> " + month +
                           " det är år >> " + year);
        this.checkYear = year;

        // Räknar fram 30 dagar framåt vilket datum år osv...
        final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
        long offset = date.getTime();
        offset += antalDagar * MILLIS_PER_DAY;
        date.setTime(offset);
        cal.setTime(date);

        // Now get the adjusted date back
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);

        this.dayAfter = day;
        this.monthAfter = month;
        this.yearAfter = year;

    }

    private String regFromTextFile() { // Läser textfilen tmp.txt
        InputStream is = getClass().getResourceAsStream("tmp.txt");
        try {
            StringBuffer sb = new StringBuffer();
            int chr, i = 0;
            // Read until the end of the stream
            while ((chr = is.read()) != -1) {
                sb.append((char) chr);
            }

            return sb.toString();
        } catch (Exception e) {
            System.out.println("Unable to create stream");
        }
        return null;
    }

    public String setViewDateString() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException {

        //ViewDateString

        String e1 = getDate();
        String e2 = setMounth();
        String e3 = getYear();

        ViewDateString = e1 + " " + e2 + " " + e3;

        return ViewDateString;

    }

    public String setMounth() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        setViewMounth = getMounth();

        if (setViewMounth.equals("0")) {

            this.setViewMounth = "Januari";
        }
        if (setViewMounth.equals("1")) {

            this.setViewMounth = "Februari";
        }
        if (setViewMounth.equals("2")) {

            this.setViewMounth = "Mars";
        }
        if (setViewMounth.equals("3")) {

            this.setViewMounth = "April";
        }
        if (setViewMounth.equals("4")) {

            this.setViewMounth = "Maj";
        }
        if (setViewMounth.equals("5")) {

            this.setViewMounth = "Juni";
        }
        if (setViewMounth.equals("6")) {

            this.setViewMounth = "Juli";
        }
        if (setViewMounth.equals("7")) {

            this.setViewMounth = "Augusti";
        }
        if (setViewMounth.equals("8")) {

            this.setViewMounth = "September";
        }
        if (setViewMounth.equals("9")) {

            this.setViewMounth = "Oktober";
        }
        if (setViewMounth.equals("10")) {

            this.setViewMounth = "November";
        }
        if (setViewMounth.equals("11")) {

            this.setViewMounth = "December";
        }

        String viewMounth = setViewMounth;

        return viewMounth;
    }

    public String checkDay() {

        String mobileClock = today.toString(); // Tilldelar mobileClock 'todays' datumvärde, skickar och gör om till en string av java.lang.string-typ

        String checkDayString = mobileClock.substring(8, 10); // plockar ut 'datum' tecken ur klockan

        if (checkDayString.equals("01")) {

            checkDayString = "1";

        } else if (checkDayString.equals("02")) {

            checkDayString = "2";

        } else if (checkDayString.equals("03")) {

            checkDayString = "3";

        } else if (checkDayString.equals("04")) {

            checkDayString = "4";

        } else if (checkDayString.equals("05")) {

            checkDayString = "5";

        } else if (checkDayString.equals("06")) {

            checkDayString = "6";

        } else if (checkDayString.equals("07")) {

            checkDayString = "7";

        } else if (checkDayString.equals("08")) {

            checkDayString = "8";

        } else if (checkDayString.equals("09")) {

            checkDayString = "9";

        }

        String useStringDate = checkDayString;

        return useStringDate;

    }

    public String checkMounth() {

        String mobileClock = today.toString(); // Tilldelar mobileClock 'todays' datumvärde, skickar och gör om till en string av java.lang.string-typ

        String checkMounthString = mobileClock.substring(4, 7); // plockar ut 'Månad' tecken ur klockan

        if (checkMounthString.equals("Jan")) {

            checkMounthString = "0";

        } else if (checkMounthString.equals("Feb")) {

            checkMounthString = "1";

        } else if (checkMounthString.equals("Mar")) {

            checkMounthString = "2";

        } else if (checkMounthString.equals("Apr")) {

            checkMounthString = "3";

        } else if (checkMounthString.equals("May")) {

            checkMounthString = "4";

        } else if (checkMounthString.equals("Jun")) {

            checkMounthString = "5";

        } else if (checkMounthString.equals("Jul")) {

            checkMounthString = "6";

        } else if (checkMounthString.equals("Aug")) {

            checkMounthString = "7";

        } else if (checkMounthString.equals("Sep")) {

            checkMounthString = "8";

        } else if (checkMounthString.equals("Oct")) {

            checkMounthString = "9";

        } else if (checkMounthString.equals("Nov")) {

            checkMounthString = "10";

        } else if (checkMounthString.equals("Dec")) {

            checkMounthString = "11";

        }

        String useStringMounth = checkMounthString;

        return useStringMounth;

    }

    public void minimaze() {
        Display.getDisplay(this).setCurrent(null);
    }

    protected void start() {
        if (addrBook == null) {
            genMainScr();
        } else {
            genMainScr();
        }
    }

    /**
     * Display an Alert on the screen
     *
     * @param type
     *            One of the following: ERROR, INFO
     * @param msg
     *            Message to display
     * @param s
     *            screen to change to after displaying alert. if null, revert to
     *            main screen
     */
    private void displayAlert(int type, String msg, Screen s) {
        alert.setString(msg);

        switch (type) {
        case ERROR:
            alert.setTitle("Error!");
            alert.setType(AlertType.ERROR);
            break;
        case INFO:
            alert.setTitle("Info");
            alert.setType(AlertType.INFO);
            break;
        }
        Display.getDisplay(this).setCurrent(alert,
                                            s == null ? display.getCurrent() :
                                            s);

    }

    /**
     * Notify the system that we are exiting.
     */
    private void midletExit() {
        destroyApp(false);
        notifyDestroyed();
    }

    /**
     * Create the first screen of our MIDlet. This screen is a list.
     *
     * @return Screen
     */
    public Screen genMainScr() {
        if (mainScr == null) {
            mainScr = new List("Menu", List.IMPLICIT, mainScrChoices, null);
            mainScr.addCommand(cmdSelect);
            mainScr.addCommand(cmdExit);
            mainScr.setCommandListener(this);
        }
        display.setCurrent(mainScr);
        return mainScr;
    }

    /**
     * Sort order option screen. Allows us to set sort order to either sorting
     * by last name (default), or first name.
     *
     * @return Screen
     */
    private Screen genOptionScr() {
        if (optionScr == null) {
            optionScr = new Form("Options");
            optionScr.addCommand(cmdBack);
            optionScr.setCommandListener(this);

            sortChoice = new ChoiceGroup("Sort by", Choice.EXCLUSIVE);
            sortChoice.append("First name", null);
            sortChoice.append("Last name", null);
            sortChoice.setSelectedIndex(sortOrder, true);
            optionScr.append(sortChoice);
            optionScr.setItemStateListener(this);
        }
        display.setCurrent(optionScr);
        return optionScr;
    }

    /**
     * Search screen. Displays two <code>TextField</code>s: one for first name,
     * and one for last name. These are used for searching the address book.
     *
     * @see AddressBookMIDlet#genNameScr
     * @return Screen
     */
    private Screen genSearchScr() {
        if (searchScr == null) {
            searchScr = new Form("Search");
            searchScr.addCommand(cmdBack);
            searchScr.addCommand(cmdSearchNetwork);
            searchScr.addCommand(cmdSearchLocal);
            searchScr.setCommandListener(this);
            s_firstName = new TextField("First name:", "", FN_LEN,
                                        TextField.ANY);
            s_lastName = new TextField("Last name:", "", LN_LEN, TextField.ANY);
            searchScr.append(s_firstName);
            searchScr.append(s_lastName);
        }

        s_firstName.delete(0, s_firstName.size());
        s_lastName.delete(0, s_lastName.size());
        display.setCurrent(searchScr);
        return searchScr;
    }

    /**
     * Name/Phone number entry screen Displays three <code>TextField</code>s:
     * one for first name, one for last name, and one for phone number. These
     * are used to capture data to add to the address book.
     *
     * @see AddressBookMIDlet#addEntry
     * @return Screen
     */
    private Screen genEntryScr() {
        if (entryScr == null) {
            entryScr = new Form("Add new");
            entryScr.addCommand(cmdCancel);
            entryScr.addCommand(cmdAdd);
            entryScr.setCommandListener(this);

            e_firstName = new TextField("First name:", "", FN_LEN,
                                        TextField.ANY);
            e_lastName = new TextField("Last name:", "", LN_LEN, TextField.ANY);
            e_phoneNum = new TextField("Phone Number", "", PN_LEN,
                                       TextField.PHONENUMBER);
            entryScr.append(e_firstName);
            entryScr.append(e_lastName);
            entryScr.append(e_phoneNum);
        }

        e_firstName.delete(0, e_firstName.size());
        e_lastName.delete(0, e_lastName.size());
        e_phoneNum.delete(0, e_phoneNum.size());

        Display.getDisplay(this).setCurrent(entryScr);
        return entryScr;
    }

    /**
     * Generates a list of first/last/phone numbers. Can be called as a result
     * of a browse command (genBrowseScr) or a search command (genSearchScr).
     * title title of this screen (since it can be called from a browse or a
     * search command. f if not null, first name to search on l if not null,
     * last name to search on
     *
     * @param title String
     * @param f String
     * @param l String
     * @param local boolean
     * @return Screen
     */
    private Screen genNameScr(String title, String f, String l, boolean local) {
        SimpleComparator sc;
        SimpleFilter sf = null;
        RecordEnumeration re;
        phoneNums = null;

        if (local) {
            sc = new SimpleComparator(
                    sortOrder == 0 ? SimpleComparator.SORT_BY_FIRST_NAME
                    : SimpleComparator.SORT_BY_LAST_NAME);

            try {
                re = addrBook.enumerateRecords(sf, sc, false);
            } catch (Exception e) {
                displayAlert(ERROR, "Could not create enumeration: " + e, null);
                return null;
            }
        } else {
            re = new NetworkQuery(f, l, sortOrder);
        }

        nameScr = null;
        if (re.hasNextElement()) {
            nameScr = new List(title, List.IMPLICIT);
            nameScr.addCommand(cmdBack);
            nameScr.addCommand(cmdDial);
            nameScr.addCommand(emptyCallListCommand);
            nameScr.setCommandListener(this);
            phoneNums = new Vector(6);

            try {
                re = addrBook.enumerateRecords(null, null, false);
                while (re.hasNextElement()) {

                    byte[] b = re.nextRecord();

                    String pn = SimpleRecord.getPhoneNum(b);
                    nameScr.append(SimpleRecord.getPhoneNum(b) + " " +
                                   SimpleRecord.getLastName(b) + " " +
                                   SimpleRecord.getFirstName(b), null);

                    System.out.println("Record: " + pn);
                    phoneNums.addElement(pn);

                }
            } catch (Exception e) {
                displayAlert(ERROR, "Error while building name list: " + e,
                             null);
                return null;
            }
            Display.getDisplay(this).setCurrent(nameScr);
        } else {
            displayAlert(INFO, "Samtalslistan är tom!", mainList);
        }

        return nameScr;
    }


    public String checkValidateNumber(String s){

        String validateNumber = s;

        if (validateNumber.equals(validateNumber.substring(0, 1)) ||
           validateNumber.equals(validateNumber.substring(0, 2)) ||
           validateNumber.equals(validateNumber.substring(0, 3)) ||
           validateNumber.equals(validateNumber.substring(0, 4))) {

           accessNumber = "";

           System.out.println("Internnummer: " + validateNumber);
    } else {
        try {
            accessNumber = getAccessNumber();
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
    }

    return validateNumber;
    }

    /**
     * Generate a screen with which to dial the phone. Note: this may or may not
     * be implemented on a given implementation.
     *
     * @throws RecordStoreNotOpenException
     * @throws InvalidRecordIDException
     * @throws RecordStoreException
     */
    private void genDialScr() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        String checkSwitchboardNumber = getSwitchBoardType();
        String validateNumber = (String) phoneNums.elementAt(nameScr.getSelectedIndex());
        validateNumber = checkValidateNumber(validateNumber);

        try {
            if (checkSwitchboardNumber.equals("3")) {

                platformRequest("tel:" + switchBoardNumber + setP + accessNumber +
                            validateNumber); // dial the number > DTMF-signals.


            }
            if (checkSwitchboardNumber.equals("4")) {

                platformRequest("tel:" + switchBoardNumber + setP + extensionNumber + "*" +
                                                pinCodeNumber + "#" + accessNumber +
                            validateNumber); // dial the number > DTMF-signals.

            }
        } catch (ConnectionNotFoundException ex) {
        }
        }

        /**
       * Add an entry to the address book. Called after the user selects the
       * addCmd while in the genEntryScr screen.
       */
      private void addEntry() {
        String f = e_firstName.getString();
        String l = e_lastName.getString();
        String p = e_phoneNum.getString();

        byte[] b = SimpleRecord.createRecord(f, l, p);
        try {
          addrBook.addRecord(b, 0, b.length);
          displayAlert(INFO, "Record added", mainScr);
        } catch (RecordStoreException rse) {
          displayAlert(ERROR, "Could not add record" + rse, mainScr);
        }
      }


    /**
     * Gets called when the user is viewing the sort options in the optionScr.
     * Takes the new selected index and changes the sort order (how names are
     * displayed from a search or a browse). item An item list
     *
     * @param item Item
     */
    public void itemStateChanged(Item item) {
      if (item == sortChoice) {
        sortOrder = sortChoice.getSelectedIndex();
      }
    }



}


//--------------NEW CLASS ------------------------------------------------------



class SimpleComparator implements RecordComparator {

    /**
     * Sorting values (sort by first or last name)
     */
    public final static int SORT_BY_FIRST_NAME = 1;

    public final static int SORT_BY_LAST_NAME = 2;

    /**
     * Sort order. Set by constructor.
     */
    private int sortOrder = -1;

    /**
     * Public constructor: sets the sort order to be used for this
     * instantiation. Sanitize s: if it is not one of the valid sort codes, set
     * it to SORT_BY_LAST_NAME silently. s the desired sort order
     *
     * @param s int
     */
    SimpleComparator(int s) {
        switch (s) {
        case SORT_BY_FIRST_NAME:
        case SORT_BY_LAST_NAME:
            this.sortOrder = s;
            break;
        default:
            this.sortOrder = SORT_BY_LAST_NAME;
            break;
        }
    }

    /**
     * This is the compare method. It takes two records, and depending on the
     * sort order extracts and lexicographically compares the subfields as two
     * Strings. r1 First record to compare r2 Second record to compare return
     * one of the following: RecordComparator.PRECEDES if r1 is
     * lexicographically less than r2 RecordComparator.FOLLOWS if r1 is
     * lexicographically greater than r2 RecordComparator.EQUIVALENT if r1 and
     * r2 are lexicographically equivalent
     *
     * @param r1 byte[]
     * @param r2 byte[]
     * @return int
     */
    public int compare(byte[] r1, byte[] r2) {

        String n1 = null;
        String n2 = null;

        // Based on sortOrder, extract the correct fields
        // from the record and convert them to lower case
        // so that we can perform a case-insensitive compare.
        if (sortOrder == SORT_BY_FIRST_NAME) {
            n1 = SimpleRecord.getFirstName(r1).toLowerCase();
            n2 = SimpleRecord.getFirstName(r2).toLowerCase();
        } else if (sortOrder == SORT_BY_LAST_NAME) {
            n1 = SimpleRecord.getLastName(r1).toLowerCase();
            n2 = SimpleRecord.getLastName(r2).toLowerCase();
        }

        int n = n1.compareTo(n2);
        if (n < 0) {
            return RecordComparator.PRECEDES;
        }
        if (n > 0) {
            return RecordComparator.FOLLOWS;
        }

        return RecordComparator.EQUIVALENT;
    }
}


final class SimpleRecord {

    private final static int FIRST_NAME_INDEX = 0;

    private final static int LAST_NAME_INDEX = 20;

    private final static int FIELD_LEN = 20;

    private final static int PHONE_INDEX = 40;

    private final static int MAX_REC_LEN = 60;

    private static StringBuffer recBuf = new StringBuffer(MAX_REC_LEN);

    // Don't let anyone instantiate this class
    private SimpleRecord() {
    }

    // Clear internal buffer
    private static void clearBuf() {
        for (int i = 0; i < MAX_REC_LEN; i++) {
            recBuf.insert(i, ' ');
        }
        recBuf.setLength(MAX_REC_LEN);
    }

    /**
     * Takes component parts and return a record suitable for our address book.
     * return byte[] the newly created record first record field: first name
     * last record field: last name num record field: phone number
     *
     * @param first String
     * @param last String
     * @param num String
     * @return byte[]
     */
    public static byte[] createRecord(String first, String last, String num) {
        clearBuf();
        recBuf.insert(FIRST_NAME_INDEX, first);
        recBuf.insert(LAST_NAME_INDEX, last);
        recBuf.insert(PHONE_INDEX, num);
        recBuf.setLength(MAX_REC_LEN);
        return recBuf.toString().getBytes();
    }

    /**
     * Extracts the first name field from a record. return String contains the
     * first name field b the record to parse
     *
     * @param b byte[]
     * @return String
     */
    public static String getFirstName(byte[] b) {
        return new String(b, FIRST_NAME_INDEX, FIELD_LEN).trim();
    }

    /**
     * Extracts the last name field from a record. return String contains the
     * last name field b the record to parse
     *
     * @param b byte[]
     * @return String
     */
    public static String getLastName(byte[] b) {
        return new String(b, LAST_NAME_INDEX, FIELD_LEN).trim();
    }

    /**
     * Extracts the phone number field from a record. return String contains the
     * phone number field b the record to parse
     *
     * @param b byte[]
     * @return String
     */
    public static String getPhoneNum(byte[] b) {
        return new String(b, PHONE_INDEX, FIELD_LEN).trim();
    }
}


class SimpleFilter implements RecordFilter {

    // first and last names on which to filter
    private String first;

    private String last;

    /**
     * Public constructor: stores the first and last names on which to filter.
     * Stores first/last names as lower case so that filters are are
     * case-insensitive.
     *
     * @param f String
     * @param l String
     */
    public SimpleFilter(String f, String l) {
        first = f.toLowerCase();
        last = l.toLowerCase();
    }

    /**
     * Takes a record, (r), and checks to see if it matches the first and last
     * name set in our constructor. Extracts the first and last names from the
     * record, converts them to lower case, then compares them with the values
     * extracted from the record. return true if record matches, false otherwise
     *
     * @param r byte[]
     * @return boolean
     */
    public boolean matches(byte[] r) {

        String f = SimpleRecord.getFirstName(r).toLowerCase();
        String l = SimpleRecord.getLastName(r).toLowerCase();

        return f.startsWith(first) && l.startsWith(last);
    }
}


/*
 * Copyright (c) 2000-2001 Sun Microsystems, Inc. All Rights Reserved.
 */

/*
 * Class to query a network service for address book entries and parse the
 * result. Uses HttpConnection to fetch the entries from a server.
 *
 * The http request is made using a base url provided by the caller with the
 * query arguments for last name and first name encoded in the query parameters
 * of the URL.
 */

class NetworkQuery implements RecordEnumeration {
    private StringBuffer buffer = new StringBuffer(60);

    private String[] fields = new String[3];

    private String empty = new String();

    private Vector results = new Vector(20);

    private Enumeration resultsEnumeration;

    final static String baseurl = "http://127.0.0.1:8080/Book/netaddr";

    /**
     * Create a RecordEnumeration from the network. Query a network service for
     * addresses matching the specified criteria. The base URL of the service
     * has the query parameters appended. The request is made and the contents
     * parsed into a Vector which is used as the basis of the RecordEnumeration.
     * lastname the last name to search for firstname the first name to search
     * for sortorder the order in which to sort 1 is by last name, 0 is by first
     * name
     *
     * @param firstname String
     * @param lastname String
     * @param sortorder int
     */
    NetworkQuery(String firstname, String lastname, int sortorder) {
        HttpConnection c = null;
        int ch;
        InputStream is = null;
        InputStreamReader reader;
        String url;

        // Format the complete URL to request
        buffer.setLength(0);
        buffer.append(baseurl);
        buffer.append("?last=");
        buffer.append((lastname != null) ? lastname : empty);
        buffer.append("&first=");
        buffer.append((firstname != null) ? firstname : empty);
        buffer.append("&sort=" + sortorder);

        url = buffer.toString();

        // Open the connection to the service
        try {
            c = open(url);
            results.removeAllElements();

            /*
             * Open the InputStream and construct a reader to convert from bytes
             * to chars.
             */
            is = c.openInputStream();
            reader = new InputStreamReader(is);
            while (true) {
                int i = 0;
                fields[0] = empty;
                fields[1] = empty;
                fields[2] = empty;
                do {
                    buffer.setLength(0);
                    while ((ch = reader.read()) != -1 && (ch != ',')
                                 && (ch != '\n')) {
                        if (ch == '\r') {
                            continue;
                        }
                        buffer.append((char) ch);
                    }

                    if (ch == -1) {
                        throw new EOFException();
                    }

                    if (buffer.length() > 0) {
                        if (i < fields.length) {
                            fields[i++] = buffer.toString();
                        }
                    }
                } while (ch != '\n');

                if (fields[0].length() > 0) {
                    results.addElement(SimpleRecord.createRecord(fields[0],
                            fields[1], fields[2]));
                }
            }
        } catch (Exception e) {

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (c != null) {
                    c.close();
                }
            } catch (Exception e) {
            }
        }
        resultsEnumeration = results.elements();
    }

    /**
     * Read the HTTP headers and the data using HttpConnection. Check the
     * response code to ensure successful open. Connector.open is used to open
     * url and a HttpConnection is returned. The HTTP headers are read and
     * processed. url the URL to open throws IOException for any network related
     * exception
     *
     * @param url String
     * @throws IOException
     * @return HttpConnection
     */
    private HttpConnection open(String url) throws IOException {
        HttpConnection c;
        int status = -1;

        // Open the connection and check for redirects
        while (true) {
            c = (HttpConnection) Connector.open(url);

            // Get the status code,
            // causing the connection to be made
            status = c.getResponseCode();

            if ((status == HttpConnection.HTTP_TEMP_REDIRECT)
                || (status == HttpConnection.HTTP_MOVED_TEMP)
                || (status == HttpConnection.HTTP_MOVED_PERM)) {

                // Get the new location and close the connection
                url = c.getHeaderField("location");
                c.close();
            } else {
                break;
            }
        }

        // Only HTTP_OK (200) means the content is returned.
        if (status != HttpConnection.HTTP_OK) {
            c.close();
            throw new IOException("Response status not OK");
        }
        return c;
    }

    /**
     * Returns true if more elements exist in enumeration.
     *
     * @return boolean
     */
    public boolean hasNextElement() {
        return resultsEnumeration.hasMoreElements();
    }

    /**
     * Returns a copy of the next record in this enumeration,
     *
     * @return byte[]
     */
    public byte[] nextRecord() {
        return (byte[]) resultsEnumeration.nextElement();
    }

    /**
     * The following are simply stubs that we don't implement...
     *
     * @return boolean
     */
    public boolean hasPreviousElement() {
        return false;
    }

    public void destroy() {
    }

    public boolean isKeptUpdated() {
        return false;
    }

    public void keepUpdated(boolean b) {
        return;
    }

    public int nextRecordId() {
        return 0;
    }

    public int numRecords() {
        return 0;
    }

    public byte[] previousRecord() {
        return null;
    }

    public int previousRecordId() {
        return 0;
    }

    public void rebuild() {
        return;
    }

    public void reset() {
        return;
    }
}
