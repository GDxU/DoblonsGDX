var UTILS = {
	randInt : function(a, b) {
		return Math.floor(Math.random() * (b - a + 1)) + a
	},
	randFloat : function(a, b) {
		return a + Math.random() * (b - a)
	},
	nearestPow2 : function(a) {
		return Math.pow(2, Math.round(Math.log(a) / Math.log(2)))
	},
	getDistance : function(a, b, c, d) {
		return Math.sqrt((c -= a) * c + (d -= b) * d)
	},
	getDirection : function(x1, y1, x2, y2) {
		return Math.atan2(y1 - y2, x1 - x2);
	},
	padNum : function(a, b) {
		for (var c = a + ""; c.length < b;)
			c = "0" + c;
		return c
	},
	getTimeString : function(a) {
		var b = parseInt(a % 1E3 / 100);
		a = parseInt(a / 1E3);
		return this.padNum(a, 2) + ":" + this.padNum(b, 2)
	},
	getUniqueID : function() {
		return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
		function(a) {
			var b = 16 * Math.random() | 0;
			return ("x" == a ? b : b & 3 | 8).toString(16)
		})
	}
};