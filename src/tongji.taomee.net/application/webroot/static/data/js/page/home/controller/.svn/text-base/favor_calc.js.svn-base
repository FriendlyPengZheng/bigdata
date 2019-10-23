window.onload = function() {
	$('td').bind("contextmenu", function () {
		return false;
	}).mousedown(function (event) {
		if (event.which === 1) {
			if (typeof parseFloat($(this).text()) !== NaN) {
				$(this).toggleClass('selected');
			}
		} else if (event.which === 3) {
			var seq = [];
			if ($(this).hasClass('selected')) {
				$('td').each(function () {
					if ($(this).hasClass('selected')) {
						seq.push(parseFloat($(this).text()));
					}
				});
			}
		}
	});	
};

var _min = function (seq) {
	var min = seq[0];
	for(var i = 0; i < seq.length; i++)
		if (seq[i] < min)
			min = seq[i];
	return min;
};

var _max = function (seq) {
	var max = seq[0];
	for(var i = 0; i < seq.length; i++)
		if (seq[i] > max)
			max = seq[i];
	return max;
};

var _sum = function (seq) {
	var total = 0;
	for(var i = 0; i < seq.length; i++)
		total += seq[i];
	return total;
};

var _average = function (seq) {
	return _sum(seq) / seq.length;
};
