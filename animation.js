// Globals

var count = 0;
var n = 23;

var SVG_WIDTH;
var SVG_HEIGHT;

var CELL_WIDTH;
var CELL_HEIGHT;
var CELL_PADDING;
var FREQUENCY_FONT_SIZE;
var DAY_NO_FONT_SIZE;
var MONTH_NAME_FONT_SIZE;
var BALL_START_X;
var BALL_START_Y;
var BALL_START_INIT_Y;

var toId;

var months = [ "January", "February", "March", "April",
	       "May", "June", "July", "August", "September",
	       "October", "November", "December" ];

var daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var cutOff = daysInMonth[0] + daysInMonth[1] + daysInMonth[2]+ daysInMonth[3]+ 
    daysInMonth[4]+ daysInMonth[5];

var colors = ["yellow", "red", "blue", "green", "orange", "violet",  "silver", "gold"];

// End of Globals

// Functions

// Initialize the variables

function Init() {
    var svg = document.getElementById("BDaySVG");
    SVG_WIDTH = svg.getAttribute("width") * 1;
    SVG_HEIGHT = svg.getAttribute("height") * 1;

    var elt = document.getElementById("cell");
    CELL_WIDTH = elt.getAttribute("width") * 1;
    CELL_HEIGHT = elt.getAttribute("height") * 1;
    
    elt = document.getElementById("CELL_PADDING");
    CELL_PADDING = elt.firstChild.nodeValue * 1;
    
    elt = document.getElementById("FREQUENCY_FONT_SIZE");
    FREQUENCY_FONT_SIZE = elt.firstChild.nodeValue * 1;

    elt = document.getElementById("DAY_NO_FONT_SIZE");
    DAY_NO_FONT_SIZE = elt.firstChild.nodeValue * 1;
    
    elt = document.getElementById("MONTH_NAME_FONT_SIZE");
    MONTH_NAME_FONT_SIZE = elt.firstChild.nodeValue * 1;

    BALL_START_X = SVG_WIDTH / 2;

    elt = document.getElementById("BALL_START_Y");
    BALL_START_Y = elt.firstChild.nodeValue * 1;

    elt = document.getElementById("BALL_START_INIT_Y");
    BALL_START_INIT_Y = elt.firstChild.nodeValue * 1;
}


// Return a random number 0 to n-1
function RandomNumber(n){
    return Math.floor(n * Math.random()) % n;
}


// Return a cell id given day (0--364)
function getDayName(x) {
    return months[getMonthNumber(x)] + getDayNumber(x);
}

// Return a month number (0--11) given day (0--364)
function getMonthNumber(x) {
    var i = 0;
    var y = x;
    while (y > daysInMonth[i]) {
	y -= daysInMonth[i];
	i++;
    } 
    return i;
}

// Return day in month (0--27, 29 or 30) given day (0--364)
function getDayNumber(x) {
    var i = 0;
    var y = x;
    while (y >= daysInMonth[i]) {
	y -= daysInMonth[i];
	i++;
    } 
    return y;
}

function Start() {
    StartBall();
}

function StartBall() {
    count++;
    var background = document.getElementById("background");
    var p = background.parentNode;
    var birthday = RandomNumber(365);
    var monthNumber = getMonthNumber(birthday);
    var dayNumber = getDayNumber(birthday);
    var dayName = months[monthNumber] + dayNumber;
    var cell = document.getElementById(dayName + "cell");
    
    var targetX = 0;
    var targetY;
    if (monthNumber < 6) {
	targetY = (2 * monthNumber * (CELL_HEIGHT + CELL_PADDING) + monthNumber * MONTH_NAME_FONT_SIZE + 32);
    } else {
	targetX += SVG_WIDTH / 2;
	targetY = (2 * (monthNumber - 6) * (CELL_HEIGHT + CELL_PADDING) + 
		   (monthNumber - 6) * MONTH_NAME_FONT_SIZE + 32);
    }
    
    if (dayNumber < 15) {
	targetX += dayNumber * CELL_WIDTH + CELL_PADDING;
	targetY += CELL_HEIGHT + MONTH_NAME_FONT_SIZE + 6;	
    } else {
	targetX += (dayNumber - 15) * CELL_WIDTH + CELL_PADDING;
	targetY += 2* CELL_HEIGHT + MONTH_NAME_FONT_SIZE + 6;
    }
    
    targetX += CELL_WIDTH / 2;
    targetY += CELL_HEIGHT / 2;
    
    var path = "M " + BALL_START_X + "," + BALL_START_Y + 
 	" L " + BALL_START_X + "," + BALL_START_INIT_Y + " T";
    path += " " + targetX + "," + targetY;

    var c = document.getElementById("cloneMe").cloneNode(true);;
    c.setAttribute("id", dayName);
    var motion = c.getElementsByTagName("animateMotion").item(0);
    motion.setAttribute("path", path);
    c.appendChild(motion);
    p.appendChild(c);
    motion.beginElement();
    return;
}

function DeleteMe(evt) {
    var p = evt.target.parentNode;
    var dayName = p.getAttribute("id");

    if (toId != null) {
    	clearTimeout(toId);
    }
    var gp = p.parentNode;
    gp.removeChild(p);

    var freq = document.getElementById(dayName + "frequency");
    freq.firstChild.nodeValue++;
    var c = freq.firstChild.nodeValue;
    var cell = document.getElementById(dayName + "cell");
    if ( c > 1) {
	cell.setAttribute("fill", "red");
    } else {
	cell.setAttribute("fill", "blue");
    }
    if (count < n) {
 	toId = setTimeout("StartBall()", 10);
    }
}
