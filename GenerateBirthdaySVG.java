/**
 * GenerateBirthdaySVG.java
 *
 *
 * Created: Tue Aug  6 21:16:07 2002
 *
 * @author <a href="http://www-stat.Stanford.EDU/~naras">Balasubramanian Narasimhan</a>
 * @version $Id: GenerateBirthdaySVG.java,v 1.7 2002/08/19 05:18:27 naras Exp naras $
 */

import java.io.FileWriter;
import java.io.IOException;

import org.apache.batik.svggen.SVGGraphics2D;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.svg.GenericCDATASection;
import org.apache.batik.dom.svg.AbstractDocument;

import java.io.*;


public class GenerateBirthdaySVG{

    public GenerateBirthdaySVG () {

    }


    public static final String[] months = { "January", "February", "March", "April",
					    "May", "June", "July", "August", "September",
					    "October", "November", "December" };

    public static final int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31,
					     30, 31, 30, 31};

    public static final int SVG_WIDTH = 980;
    public static final int SVG_HEIGHT = 650;

    public static final String BALL_STROKE_COLOR = "blue";
    public static final String BALL_FILL_COLOR = "RED";
    public static final int BALL_STROKE_WIDTH = 1;

    public static final String CELL_STROKE_COLOR = "black";
    public static final String CELL_FILL_COLOR = "white";
    public static final int CELL_STROKE_WIDTH = 1;
    public static final int CELL_WIDTH = 30;
    public static final int CELL_HEIGHT = 30;
    public static final int CELL_PADDING = 5;

    public static final int FREQUENCY_FONT_SIZE = 10;
    public static final int DAY_NO_FONT_SIZE = 12;
    public static final int MONTH_NAME_FONT_SIZE = 14;

    public static final int BALL_START_Y = 30;
    public static final int BALL_START_INIT_Y = 150;

    public static final String BIRTHDAY_ANIMATION_SCRIPT = "animation.js";

    /**
     * Generates the Birthday SVG
     */

    public static void main(String[] args){

	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	Document doc = impl.createDocument(svgNS, "svg", null);

	// get the root element (the svg element)
	Element svgRoot = doc.getDocumentElement();

	// set the width and height attribute on the root svg element
	svgRoot.setAttributeNS(null, "width", "" + SVG_WIDTH);
	svgRoot.setAttributeNS(null, "height", "" + SVG_HEIGHT);
	svgRoot.setAttributeNS(null, "id", "BDaySVG");
	svgRoot.setAttributeNS(null, "onload", "Init()");


	Element defs = doc.createElementNS(svgNS, "defs");

	// Define some constants that our scripts can use

	Element param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "CELL_PADDING");
	param.appendChild(new GenericCDATASection("" + CELL_PADDING, (AbstractDocument) doc));
	defs.appendChild(param);

	param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "FREQUENCY_FONT_SIZE");
	param.appendChild(new GenericCDATASection("" + FREQUENCY_FONT_SIZE, (AbstractDocument) doc));
	defs.appendChild(param);

	param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "DAY_NO_FONT_SIZE");
	param.appendChild(new GenericCDATASection("" + DAY_NO_FONT_SIZE, (AbstractDocument) doc));
	defs.appendChild(param);

	param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "MONTH_NAME_FONT_SIZE");
	param.appendChild(new GenericCDATASection("" + MONTH_NAME_FONT_SIZE, (AbstractDocument) doc));
	defs.appendChild(param);

	param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "BALL_START_Y");
	param.appendChild(new GenericCDATASection("" + BALL_START_Y, (AbstractDocument) doc));
	defs.appendChild(param);

	param = doc.createElementNS(svgNS, "text");
	param.setAttributeNS(null, "id", "BALL_START_INIT_Y");
	param.appendChild(new GenericCDATASection("" + BALL_START_INIT_Y, (AbstractDocument) doc));
	defs.appendChild(param);


	// Insert the script
	svgRoot.appendChild(generateScript(svgNS, doc));


	// Define the objects that we will reuse
	Element ball = doc.createElementNS(svgNS, "circle");
	ball.setAttributeNS(null, "stroke-width", "" + BALL_STROKE_WIDTH);
	ball.setAttributeNS(null, "stroke", BALL_STROKE_COLOR);
	ball.setAttributeNS(null, "fill", BALL_FILL_COLOR);
	ball.setAttributeNS(null, "cx", "0");
	ball.setAttributeNS(null, "cy", "0");
	ball.setAttributeNS(null, "r", "10");
	ball.setAttributeNS(null, "id", "Ball");


	// Define the animatable ball that we will clone throughout
	Element animatableBall = doc.createElementNS(svgNS, "use");
	animatableBall.setAttributeNS(null, "xlink:href", "#Ball");
	animatableBall.setAttributeNS(null, "id", "cloneMe");

 	Element motion = doc.createElementNS(svgNS, "animateMotion");
	motion.setAttributeNS(null, "dur", "3s");
 	motion.setAttributeNS(null, "begin", "indefinite");
 	motion.setAttributeNS(null, "onend", "DeleteMe(evt)");
 	motion.setAttributeNS(null, "fill", "freeze");
 	motion.setAttributeNS(null, "restart", "always");
 	motion.setAttributeNS(null, "accumulate", "none");
 	motion.setAttributeNS(null, "additive", "replace");
 	motion.setAttributeNS(null, "calcMode", "linear");
 	animatableBall.appendChild(motion);

	Element cell = doc.createElementNS(svgNS, "rect");
	cell.setAttributeNS(null, "stroke-width", "" + CELL_STROKE_WIDTH);
	cell.setAttributeNS(null, "stroke", CELL_STROKE_COLOR);
	cell.setAttributeNS(null, "width", "" + CELL_WIDTH);
	cell.setAttributeNS(null, "height", "" + CELL_HEIGHT);
	cell.setAttributeNS(null, "x", "0");
	cell.setAttributeNS(null, "y", "0");
	cell.setAttributeNS(null, "id", "cell");


	defs.appendChild(ball);
	defs.appendChild(animatableBall);
	defs.appendChild(cell);

	// attach the defs to the svg root element
	svgRoot.appendChild(defs);


	Element style = doc.createElementNS(svgNS, "style");
	style.setAttribute("type", "text/css");

	GenericCDATASection data =
	    new GenericCDATASection("#OneHit {fill:red}\n#MoreHits {fill:blue}\n",
				    (AbstractDocument) doc);
	style.appendChild(data);
	svgRoot.appendChild(style);

	// now create the background

	Element elt = doc.createElementNS(svgNS, "rect");
	elt.setAttributeNS(null, "id", "background");
	elt.setAttributeNS(null, "stroke-width", "4");
	elt.setAttributeNS(null, "stroke", "brown");
	elt.setAttributeNS(null, "fill", "#EEDDA0");
	elt.setAttributeNS(null, "width", "" + SVG_WIDTH);
	elt.setAttributeNS(null, "height", "" + SVG_HEIGHT);
	elt.setAttributeNS(null, "x", "0");
	elt.setAttributeNS(null, "y", "0");

	svgRoot.appendChild(elt);

	elt = doc.createElementNS(svgNS, "text");
	elt.setAttributeNS(null, "id", "title");
	elt.setAttributeNS(null, "anchor", "middle");
	elt.setAttributeNS(null, "font-size", "20");
	elt.setAttributeNS(null, "x", "" + (SVG_WIDTH / 3));
	elt.setAttributeNS(null, "y", "25");
	elt.setAttributeNS(null, "startOffset", "0");
	elt.appendChild(new GenericCDATASection("Von Mises' Birthday Problem", (AbstractDocument) doc));


	svgRoot.appendChild(elt);
	for (int i = 0; i < 6; i++) {
	    elt = generateMonthCells(i, svgNS, doc);
	    elt.setAttributeNS(null, "transform",
			       "translate(0, " + (2 * i * (CELL_HEIGHT + CELL_PADDING) + i * MONTH_NAME_FONT_SIZE + 32) + ")");
	    svgRoot.appendChild(elt);
	}
	for (int i = 0; i < 6; i++) {
	    elt = generateMonthCells(i + 6, svgNS, doc);
	    elt.setAttributeNS(null, "transform",
			       "translate(" + (SVG_WIDTH / 2) + ", "  + (2 * i * (CELL_HEIGHT + CELL_PADDING) + i * MONTH_NAME_FONT_SIZE + 32) + ")");
	    svgRoot.appendChild(elt);
	}


	elt = doc.createElementNS(svgNS, "g");
	elt.setAttributeNS(null, "shape-rendering", "optimizeSpeed");
	elt.setAttributeNS(null, "id", "Balls");
	svgRoot.appendChild(elt);

	// add the start button
	Element gElt = doc.createElementNS(svgNS, "g");
	gElt.setAttributeNS(null, "id", "startButton");
	gElt.setAttributeNS(null, "onclick", "Start()");

	elt = doc.createElementNS(svgNS, "rect");
	elt.setAttributeNS(null, "fill", "lightblue");
	elt.setAttributeNS(null, "width", "80");
	elt.setAttributeNS(null, "height", "40");
	elt.setAttributeNS(null, "x", "" + (SVG_WIDTH / 2 - 50));
	elt.setAttributeNS(null, "y", "" + (SVG_HEIGHT - 50));
	gElt.appendChild(elt);

 	elt = doc.createElementNS(svgNS, "text");
 	elt.setAttributeNS(null, "startOffset", "0");
 	elt.setAttributeNS(null, "font-size", "30");
 	elt.setAttributeNS(null, "text-anchor", "middle");
 	elt.setAttributeNS(null, "x", "" + (SVG_WIDTH /2 - 10));
 	elt.setAttributeNS(null, "y", "" + (SVG_HEIGHT - 20));
 	elt.appendChild(new GenericCDATASection("Start", (AbstractDocument) doc));
 	gElt.appendChild(elt);

	svgRoot.appendChild(gElt);

	// write out the file
	writeFile("Birthday.svg", doc);

    }


    /**
     * Generate the cells for a given month
     *
     * @param montNumber the number of the month (0-11)
     * @param svgNS, the SVG namespace
     * @param doc, the document
     * @return the SVG element that can be added to the DOM tree
     */
    public static Element generateMonthCells(int monthNumber, String svgNS,
					     Document doc) {
	int noOfdays = daysInMonth[monthNumber];
	String monthName = months[monthNumber];
	// Build first row of 15 days

	Element result = doc.createElementNS(svgNS, "g");
	result.setAttributeNS(null, "id", monthName);

	Element name = doc.createElementNS(svgNS, "text");
	name.setAttributeNS(null, "id", monthName + "name");
	name.setAttributeNS(null, "text-anchor", "middle");
	name.setAttributeNS(null, "font-family", "TimesRoman-Bold");
	name.setAttributeNS(null, "font-size", "" + MONTH_NAME_FONT_SIZE);
	name.setAttributeNS(null, "x", "" + (2 * CELL_WIDTH - MONTH_NAME_FONT_SIZE));
	name.setAttributeNS(null, "y", "" + (MONTH_NAME_FONT_SIZE + 32));
	name.setAttributeNS(null, "startOffset", "0");
	name.appendChild(new GenericCDATASection(monthName, (AbstractDocument) doc));
	result.appendChild(name);

	for (int i = 0; i < 15; i++) {
	    Element aCell = generateOneCell(i, monthName, svgNS, doc);
	    aCell.setAttributeNS(null, "transform",
				 "translate(" + (i * CELL_WIDTH + CELL_PADDING) + ", " + (CELL_HEIGHT + MONTH_NAME_FONT_SIZE + 6) + ")");
	    result.appendChild(aCell);
	}
	for (int i = 15; i < daysInMonth[monthNumber]; i++) {
	    Element aCell = generateOneCell(i, monthName, svgNS, doc);
	    aCell.setAttributeNS(null, "transform",
				 "translate(" + ((i - 15) * CELL_WIDTH + CELL_PADDING) + ", " + (2* CELL_HEIGHT + MONTH_NAME_FONT_SIZE + 6) + ")");
	    result.appendChild(aCell);

	}

	return result;
    }


    /**
     * Generate a single cell for a given month
     *
     * @param day the day number
     * @param monthName the name of the month
     * @param svgNS, the SVG namespace
     * @param doc, the document
     * @return the SVG element that can be added to the DOM tree
     */
    public static Element generateOneCell(int day, String monthName, String svgNS,
					  Document doc) {

	Element result = doc.createElementNS(svgNS, "g");
	result.setAttributeNS(null, "id", monthName + day);

	Element use = doc.createElementNS(svgNS, "use");
	use.setAttributeNS(null, "id", monthName + day + "cell");
	use.setAttributeNS(null, "x", "0");
	use.setAttributeNS(null, "y", "0");
	use.setAttributeNS(null, "fill", "white");
	use.setAttributeNS(null, "xlink:href", "#cell");
	result.appendChild(use);

	Element frequency = doc.createElementNS(svgNS, "text");
	frequency.setAttributeNS(null, "id",  monthName + day + "frequency");
	frequency.setAttributeNS(null, "text-anchor", "middle");
	frequency.setAttributeNS(null, "font-size", "" + FREQUENCY_FONT_SIZE);
	frequency.setAttributeNS(null, "x", "" + FREQUENCY_FONT_SIZE);
	frequency.setAttributeNS(null, "y", "" + (CELL_HEIGHT - 4));
	frequency.setAttributeNS(null, "startOffset", "0");
	result.appendChild(frequency);
	frequency.appendChild(new GenericCDATASection("0", (AbstractDocument) doc));

	Element legend = doc.createElementNS(svgNS, "text");
	legend.setAttributeNS(null, "id", monthName + day + "legend");
	legend.setAttributeNS(null, "text-anchor", "middle");
	legend.setAttributeNS(null, "font-family", "Verdana");
	legend.setAttributeNS(null, "font-size", "" + DAY_NO_FONT_SIZE);
	legend.setAttributeNS(null, "x", "" + (CELL_WIDTH - DAY_NO_FONT_SIZE));
	legend.setAttributeNS(null, "y", "" + (DAY_NO_FONT_SIZE + 4));
	legend.setAttributeNS(null, "startOffset", "0");
	legend.appendChild(new GenericCDATASection("" + (day + 1), (AbstractDocument) doc));
	result.appendChild(legend);

	return result;
    }


    /**
     * Write the document to a file
     *
     * @param filename, the file to write to
     * @param doc the document to write
     */
    public static void writeFile(String filename, Document doc) {
	try {
	    // Use a Transformer for output
	    TransformerFactory tFactory =
		TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();

	    DOMSource source = new DOMSource(doc);
	    FileWriter fw = new FileWriter(filename);
	    StreamResult result = new StreamResult(fw);
	    transformer.transform(source, result);
	    fw.close();
	}
	catch (IOException ioe) {
	    System.err.println(ioe.getMessage());
	    ioe.printStackTrace();
	}
	catch (TransformerConfigurationException tce) {
	    System.err.println(tce.getMessage());
	    tce.printStackTrace();
	}
	catch (TransformerException te) {
	    System.err.println(te.getMessage());
	    te.printStackTrace();
	}
    }

    /**
     * Generate a script (in an external file) section for the animation.
     *
     * @param svgNS, the SVG namespace
     * @param doc, the document
     * @return the SVG element that can be added to the DOM tree
     */
    public static Element generateScript(String svgNS, Document doc) {

	Element result = doc.createElementNS(svgNS, "g");
	result.setAttributeNS(null, "id", "scripts");
	Element script = doc.createElementNS(svgNS, "script");
	script.setAttributeNS(null, "id", "scriptCode");
	// Read the external file
	script.appendChild(new GenericCDATASection(readFileAsString(BIRTHDAY_ANIMATION_SCRIPT),
						   (AbstractDocument) doc));
	result.appendChild(script);
	return result;
    }

    /**
     * Read a file and return a string of the contents
     * @param filename the name of the file
     * @return the string contents of the file
     */
    public static String readFileAsString(String filename) {
	StringBuffer result = new StringBuffer(2048);
	try {
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = br.readLine()) != null) {
		result.append(line);
		result.append("\n");
	    }
	    br.close();
	} catch (IOException ioe) {
	    System.err.println(ioe.getMessage());
	    ioe.printStackTrace();
	}

	return result.toString();
    }

} // GenerateBirthdaySVG

