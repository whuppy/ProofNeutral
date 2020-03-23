<script type="text/javascript" src="https://www.google.com/jsapi">
</script>
<script type="text/javascript">
	google.load("visualization", "1", {
		packages : [ "corechart" ]
	});
	google.setOnLoadCallback(drawChart);
	function drawChart() {
		   var data = google.visualization.arrayToDataTable([
		                                                     ['X', 'AB', 'PQ'],
		                                                     [${abpq.a.x}, ${abpq.a.y}, null] ,
		                                                     [${abpq.b.x}, ${abpq.b.y}, null],
		                                                     [${abpq.p.x}, null, ${abpq.p.y}],
		                                                     [${abpq.q.x}, null, ${abpq.q.y}]
		                                                ]);
		var options = {
			title : 'AB and PQ',
			hAxis : {
				title : 'X',
				minValue : -100,
				maxValue : 100
			},
			vAxis : {
				title : 'Y',
				minValue : -100,
				maxValue : 100
			},
			lineWidth : 2,
		};
		var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));
		chart.draw(data, options);
	}
</script>

<html>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="EXPIRES" CONTENT="0">
<head>
<title>Distance from P to AB</title>
</head>
<body>
	<p>Given line segment AB and point P, find the point Q on AB
		closest to P.</p>
	<div id="chart_div" style="width: 400px; height: 400px;"></div>
	<p>Distance = ${abpq.distance}</p>
	<p>A is (${abpq.a.x}, ${abpq.a.y}), and baby you can guess the rest.</p>
</body>
</html>



