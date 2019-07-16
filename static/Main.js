/*Responsive design for phone/mobile devices
 * Adds navigation link element to left side of page when screen is too small for the larger sized navigation links*/
function responsiveLeftNav() 
{
    var x = document.getElementById("leftNavBar");
	
    if (x.className === "col left") {
    	x.className += " responsive";
    } else {
    	x.className = "col left";
	}
}

function dropdownResponse(className) 
{
    var x = document.getElementById("issueElement");
	
    if (x.className === className) {
    	x.className += " hideInfo";
    } else {
    	x.className = className;
	}
}