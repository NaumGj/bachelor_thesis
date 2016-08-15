app = angular.module('IoTVisual', ['ngRoute', 'ngAnimate','ngMaterial','ngResource','IoTVisual.services'])
.config(['$routeProvider', '$locationProvider',
	function($routeProvider, $locationProvider) {

		$routeProvider
		.when('/', {
			templateUrl: 'first.html',
			controller: 'FirstCtrl',
			controllerAs: 'first'
		})
        .when('/counters', {
			templateUrl: 'counters.html',
			controller: 'CountersCtrl',
			controllerAs: 'counters'
		})
		.otherwise({
			redirectTo: '/'
		});

		$locationProvider.html5Mode(false);
		$locationProvider.hashPrefix('!');
	}])
.controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log, $location) {

	$scope.toggleLeft = buildDelayedToggler('left');
	/*
	 * Supplies a function that will continue to operate until the
	 * time is up.
	 */
	 function debounce(func, wait, context) {
		var timer;
		return function debounced() {
			var context = $scope,
			args = Array.prototype.slice.call(arguments);
			$timeout.cancel(timer);
			timer = $timeout(function() {
				timer = undefined;
				func.apply(context, args);
			}, wait || 10);
		};
	 }
	/**
	 * Build handler to open/close a SideNav; when animation finishes
	 * report completion in console
	 */
	 function buildDelayedToggler(navID) {
		return debounce(function() {$mdSidenav(navID).toggle()}, 200);
	 }
})
.controller('FirstCtrl', ['$routeParams', function($routeParams) {

	this.name = "FirstCtrl";
	this.params = $routeParams;

}])
//.controller('CountersCtrl', function($routeParams, $scope, Izdelki, Test, $timeout) {
.controller('CountersCtrl', function($scope, $interval, Counter, Latency) {

	/* // Function to get the data
	$scope.getData = function(){
		Test.query().$promise.then(function(izdelek) {
			$scope.salesData = izdelek;
			console.log($scope.izdelek);
		});
	}
	
	// Function to replicate setInterval using $timeout service.
	$scope.intervalFunction = function(){
		$timeout(function() {
			$scope.getData();
			$scope.intervalFunction();
		}, 1000)
	};
	
	// Kick off the interval
	$scope.intervalFunction(); */
	/* $scope.salesData=[
		{hour: 1,sales: 54},
		{hour: 2,sales: 66},
		{hour: 3,sales: 77},
		{hour: 4,sales: 70},
		{hour: 5,sales: 60},
		{hour: 6,sales: 63},
		{hour: 7,sales: 55},
		{hour: 8,sales: 47},
		{hour: 9,sales: 55},
		{hour: 10,sales: 30}
	]; */
	var index = 0;
	$scope.latencyData = [{index: index, value: 0}];
	$scope.countData = [{index: index, value: 0}];

	$interval(function(){
		Counter.query().$promise.then(function(counts) {
			console.log(counts);
			// console.log(count[0]);
			if(counts.length > 0) {
				$scope.count = counts[counts.length - 1].counts;
			} else {
				$scope.count = 0;
			}
			$scope.countData = [];
			index = 0;
			counts.forEach(function(count) {
				// console.log(latency);
				$scope.countData.push({index: index, value: count.counts});
				index = index + 1;
			});
		});
		Latency.query().$promise.then(function(avgLatencies) {
			console.log(avgLatencies);
			// console.log(latency[0]);
			if(avgLatencies.length > 0) {
				$scope.latency = parseFloat(Math.round(avgLatencies[avgLatencies.length - 1].ten * 100) / 100).toFixed(3);
			} else {
				$scope.latency = 0;
			}
			$scope.latencyData = [];
			index = 0;
			avgLatencies.forEach(function(latency) {
				// console.log(latency);
				$scope.latencyData.push({index: index, value: latency.ten});
				index = index + 1;
			});
			/* if(latency.ten) {
				if($scope.salesData.length > 30) {
					$scope.salesData.shift();
				}
				//console.log("Pushing latency!");
				//console.log(hour);
				console.log($scope.salesData);
				$scope.salesData.push({hour: hour, sales: latency.ten});
			} */
		});
	}, 2000, 0);

})
.directive('linearChart', function($parse, $window){
   return{
	  restrict:'EA',
	  template:"<svg width='850' height='200'></svg>",
	   link: function(scope, elem, attrs){
		   var exp = $parse(attrs.chartData);

           var dataToPlot=exp(scope);
           var padding = 20;
           var pathClass="path";
           var xScale, yScale, xAxisGen, yAxisGen, lineFun;

           var d3 = $window.d3;
           var rawSvg=elem.find('svg');
           var svg = d3.select(rawSvg[0]);

           scope.$watchCollection(exp, function(newVal, oldVal){
               dataToPlot=newVal;
               redrawLineChart();
           });

           function setChartParameters(){

               xScale = d3.scale.linear()
                   .domain([0, dataToPlot.length-1])
                   .range([padding + 50, rawSvg.attr("width") - padding]);

               yScale = d3.scale.linear()
                   .domain([0, d3.max(dataToPlot, function (d) {
                       return d.value;
                   })])
                   .range([rawSvg.attr("height") - padding, 0]);

               xAxisGen = d3.svg.axis()
                   .scale(xScale)
                   .orient("bottom")
                   .ticks(0);

               yAxisGen = d3.svg.axis()
                   .scale(yScale)
                   .orient("left")
                   .ticks(5);

               lineFun = d3.svg.line()
                   .x(function (d) {
                       return xScale(d.index);
                   })
                   .y(function (d) {
                       return yScale(d.value);
                   })
                   .interpolate("basis");
           }
		 
		 function drawLineChart() {

			   setChartParameters();

			   svg.append("svg:g")
				   .attr("class", "x axis")
				   .attr("transform", "translate(0,180)")
				   .call(xAxisGen);

			   svg.append("svg:g")
				   .attr("class", "y axis")
				   .attr("transform", "translate(65,0)")
				   .call(yAxisGen);

			   svg.append("svg:path")
				   .attr({
					   d: lineFun(dataToPlot),
					   "stroke": "blue",
					   "stroke-width": 2,
					   "fill": "none",
					   "class": pathClass
				   });
		   }

		   function redrawLineChart() {

			   setChartParameters();

			   svg.selectAll("g.y.axis").call(yAxisGen);

			   svg.selectAll("g.x.axis").call(xAxisGen);

			   svg.selectAll("."+pathClass)
				   .attr({
					   d: lineFun(dataToPlot)
				   });
		   }

		   drawLineChart();
	   }
   };
});
